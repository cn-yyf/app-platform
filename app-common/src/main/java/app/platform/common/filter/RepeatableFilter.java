package app.platform.common.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletRequest;
import org.springframework.http.MediaType;
import app.platform.common.utils.StringUtils;

/**
 * Repeatable 过滤器
 * 
 * @author ruoyi
 */
public class RepeatableFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest
                && StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE))
        {
            requestWrapper = new RepeatedlyRequestWrapper((HttpServletRequest) request, response);
        }
        if (null == requestWrapper)
        {
            chain.doFilter(request, response);
        }
        else
        {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy()
    {

    }
}
