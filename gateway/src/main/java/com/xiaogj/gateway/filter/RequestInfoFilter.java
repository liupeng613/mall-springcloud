package com.xiaogj.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xiaogj.gateway.util.HttpUtil;
import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

public class RequestInfoFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInfoFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        UUID uuid = UUID.randomUUID();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        LOG.info("授权信息("+uuid+"): " + String.valueOf(authentication.getAuthorities()));
        HttpServletRequest req = (HttpServletRequest)RequestContext.getCurrentContext().getRequest();

        LOG.info("请求客户端("+uuid+"): " + HttpUtil.getIpAddress(req));
        LOG.info("请求地址("+uuid+"): " + req.getScheme() + " " + req.getRemoteAddr() + ":" + req.getRemotePort());
        StringBuilder params = new StringBuilder("?");
        Enumeration<String> names = req.getParameterNames();
        if( req.getMethod().equals("GET") ) {
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                params.append(name);
                params.append("=");
                params.append(req.getParameter(name));
                params.append("&");
            }
        }
        if (params.length() > 0) {
            params.delete(params.length()-1, params.length());
        }
        LOG.info("请求接口("+uuid+"): " + req.getMethod() + " " + req.getRequestURI() + params + " " + req.getProtocol());
        Enumeration<String> headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            String value = req.getHeader(name);
            LOG.info("请求头("+uuid+"): " + name + ":" + value);
        }
        final RequestContext ctx = RequestContext.getCurrentContext();
        if (!ctx.isChunkedRequestBody()) {
            ServletInputStream inp = null;
            try {

                inp = ctx.getRequest().getInputStream();
                String body = null;
                if (inp != null) {
                    body = IOUtils.toString(inp);
                    LOG.info("请求体("+uuid+"): > " + body);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        return null;
    }
}
