package com.forteach.quiz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

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

        registry.addResourceHandler("/swagger-ui.html**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
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

}
