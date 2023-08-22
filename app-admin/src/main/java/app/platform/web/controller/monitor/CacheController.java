package app.platform.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import app.platform.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import app.platform.common.constant.CacheConstants;
import app.platform.common.core.domain.AjaxResult;
import app.platform.common.utils.StringUtils;
import app.platform.system.domain.SysCache;

/**
 * 缓存监控
 * 
 * @author ruoyi
 */
@Tag(name = "缓存监控")
@RestController
@RequestMapping("/monitor/cache")
public class CacheController
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static List<SysCache> caches = new ArrayList<SysCache>();
    {
        caches.add(new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new SysCache(CacheConstants.SYS_CONFIG_KEY, "配置信息"));
        caches.add(new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典"));
        caches.add(new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new SysCache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
    }
    @Operation(summary = "查询缓存状态信息")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public R<Map<String, Object>> getInfo() throws Exception
    {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return R.ok(result);
    }
    @Operation(summary = "查询缓存项目名称")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getNames")
    public R<Void> cache()
    {
        return R.ok();
    }
    @Operation(summary = "查询缓存的keys")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getKeys/{cacheName}")
    public R<Set<String>> getCacheKeys(@PathVariable String cacheName)
    {
        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return R.ok(cacheKeys);
    }

    @Operation(summary = "查询缓存的值")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public R<SysCache> getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey)
    {
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        SysCache sysCache = new SysCache(cacheName, cacheKey, cacheValue);
        return R.ok(sysCache);
    }

    @Operation(summary = "删除项目名称下的缓存")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public R<Void> clearCacheName(@PathVariable String cacheName)
    {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        redisTemplate.delete(cacheKeys);
        return R.ok();
    }
    @Operation(summary = "删除某个key")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public R<Void> clearCacheKey(@PathVariable String cacheKey)
    {
        redisTemplate.delete(cacheKey);
        return R.ok();
    }
    @Operation(summary = "删除所有业务缓存")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheAll")
    public R<Void> clearCacheAll()
    {
        Collection<String> cacheKeys = redisTemplate.keys("*");
        redisTemplate.delete(cacheKeys);
        return R.ok();
    }
}
