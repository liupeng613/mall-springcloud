package com.xiaogj.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.xiaogj.gateway.util.redis.RedisService;
import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PostAuthorizedRequestFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PostAuthorizedRequestFilter.class);

    @Autowired
    private RedisService redisService;
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return   -1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if (request.getRequestURI().contains("/oauth/token")) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) request;
        String username = httpServletRequestWrapper.getRequest().getParameter("username");

        Object zuulResponse = RequestContext.getCurrentContext().get("zuulResponse");
        if (zuulResponse != null) {
            RibbonHttpResponse resp = (RibbonHttpResponse) zuulResponse;
            try {
                String body = IOUtils.toString(resp.getBody());
                context.setResponseBody(body);
                String regex = "access_token\":(.+?),";
                Matcher matcher = Pattern.compile(regex).matcher(body);
                while (matcher.find()){
                    String token = matcher.group(1);
                    token=token.replace("\"", "");
                    Object redisToken= redisService.get(username);
                    List<String> tokenList=new ArrayList();
                    if(redisToken!=null)
                    {
                       tokenList=(List<String>) (List)redisToken;
                    }
                    tokenList.add(token);
                    redisService.setRemove(username);
                    redisService.set(username,tokenList);
                    LOG.info(username+"  Token写入redis:"+token);
                    break;
                }
                return  body;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
