package com.xiaogj.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xiaogj.gateway.util.HttpUtil;
import com.xiaogj.gateway.util.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AuthorizedRequestFilter extends ZuulFilter {




    private  final  static List<String> permitAllList=new ArrayList<>(Arrays.asList("/login"
            ,".html",".css",".js",".woff2","/register/add",".ico",".png","/api-docs"));
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
    @Autowired
    private RedisService redisService;
    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
    public static boolean findContains( String s) {
        boolean flag = false;
        for(String z : permitAllList) {
            flag = flag || s.contains(z);
        }
        return flag;
    }

    @Override
    public Object run() throws ZuulException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName=String.valueOf(authentication.getPrincipal());
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest req = context.getRequest();
        String  url=req.getRequestURI();
        if(!url.contains("/oauth/token")&&!url.contains("swagger-resources")&&!findContains(url))
        {
            String header = req.getHeader("Authorization");
            String token ="" ;

            if(header!=""&&header!=null)
            {
                token=header.substring(7);

            }
            Object redisToken= redisService.get(userName);
            List<String> tokenList=new ArrayList();
            if(redisToken!=null)
            {
                tokenList=(List<String>) (List)redisToken;
            }
            if(!tokenList.contains(token)) {
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(401);
            }

        }


        String ip = HttpUtil.getIpAddress(context.getRequest());
        context.addZuulRequestHeader("X-XGJ-Client-IP", ip);
        context.addZuulRequestHeader("X-XGJ-Principal", userName);
        context.addZuulRequestHeader("X-XGJ-Authorities", String.valueOf(authentication.getAuthorities()));
        return null;
    }
}
