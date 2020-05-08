package com.xiaogj.gateway.config;

import com.xiaogj.gateway.filter.AuthorizedRequestFilter;
import com.xiaogj.gateway.filter.DeleteAuthorizedRequestFilter;
import com.xiaogj.gateway.filter.RequestInfoFilter;
import com.xiaogj.gateway.filter.PostAuthorizedRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuulConfig {
    @Bean
    public AuthorizedRequestFilter getAuthorizedRequestFilter() {
        return new AuthorizedRequestFilter() ;
    }
    @Bean
    public PostAuthorizedRequestFilter postAuthorizedRequestFilter() {
        return new PostAuthorizedRequestFilter() ;
    }
    @Bean
    public DeleteAuthorizedRequestFilter deleteAuthorizedRequestFilter() {
        return new DeleteAuthorizedRequestFilter() ;
    }

    @Bean
    public RequestInfoFilter reuestInfoFilter(){
        return new RequestInfoFilter();
    }
}
