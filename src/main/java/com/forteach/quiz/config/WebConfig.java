package com.forteach.quiz.config;

import com.forteach.quiz.config.decorator.PayloadServerWebExchangeDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域支持
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html**", "/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/");
    }

//    @Bean
//    public RouterFunction<ServerResponse> routes() {
//        return RouterFunctions.route(
//                GET("/idx"),
//                request -> ServerResponse.ok().body(BodyInserters.fromResource(new ClassPathResource("resources/static/idx.html")))
//        );
//    }

//    @Bean
//    public HandlerMapping handlerMapping() {
//        Map<String, WebSocketHandler> map = new HashMap<>(5);
//        map.put("/websocket/demo", new InteractSocketHandler());
//
//        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
//        mapping.setUrlMap(map);
//        // before annotated controllers
//        mapping.setOrder(-1);
//        return mapping;
//    }

//    @Bean
//    public WebSocketHandlerAdapter handlerAdapter() {
//        return new WebSocketHandlerAdapter();
//    }

    /**
     * TODO 输出敏感日志
     *
     * @param configurer
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().enableLoggingRequestDetails(true);
    }

    /**
     * 对请求的参数进行输出显示过滤
     *
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) //过滤器顺序
    public WebFilter webFilter() {
        return (exchange, chain) -> chain.filter(new PayloadServerWebExchangeDecorator(exchange));
    }
}
