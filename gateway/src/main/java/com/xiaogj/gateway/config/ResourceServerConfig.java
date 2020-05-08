package com.xiaogj.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        //经过oauth服务的连接全部放行,不然token会获取不到
        registry.antMatchers( "/*/oauth/**","/*/login").permitAll();
        registry.antMatchers("/**/*.html").permitAll()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/**/*.woff2").permitAll()
                .antMatchers("/**/register/add").permitAll()
                .antMatchers("/**/*.ico","/**/*.png").permitAll();
        registry.antMatchers("/**/swagger-resources/**","/**/api-docs").permitAll();
        //任何连接只要认证后放行
        registry.anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        //跟oauth中的值必须一样
        resources.resourceId("oauth2-resource");
    }

}
