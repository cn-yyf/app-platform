package app.platform.framework.security.handle;

import java.io.IOException;
import java.io.Serializable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import app.platform.common.constant.HttpStatus;
import app.platform.common.core.domain.AjaxResult;
import app.platform.common.utils.ServletUtils;
import app.platform.common.utils.StringUtils;

/**
 * 认证失败处理类 返回未授权
 *
 * @author ruoyi
 */

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final Logger log  = LoggerFactory.getLogger(AuthenticationEntryPointImpl.class);

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        log.info(msg);
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
