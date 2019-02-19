package com.forteach.quiz.filter;

import com.forteach.quiz.util.StringUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-19 09:45
 * @version: 1.0
 * @description:
 */
public class AuthFilter implements WebFilter {

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        WebClient client = WebClient.builder()
//                .filter((request, next) -> {
//                    String token = request.headers().getFirst("token");
//                    if (StringUtil.isNotEmpty(token)) {
//                        ClientRequest filtered = ClientRequest.from(request)
//                                .header("token", token)
//                                .build();
//                        next.exchange(filtered);
//                    }else {
//                        ServerHttpRequest server = exchange.getRequest().mutate().build();
//                        Map<String, String> map = new HashMap<String, String>(1);
//                        WebResult.failResult(401, "无token，请重新登录");
//                        ServerWebExchange authErrorExchange = exchange.mutate().principal(Mono.just("")).build();
//                    }
//                }).build();
//    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst("token");
        if (StringUtil.isNotEmpty(token)){
            //验证token

            return chain.filter(exchange);

        }else {
            ServerHttpRequest authErrorReq = request.mutate().path("/wechat/user/login").build();
//            authErrorReq.
//            ServerHttpResponse authErrorResq = ServerResponse.ok().
            ServerWebExchange authErrorExchange = exchange.mutate().request(authErrorReq).build();
            return chain.filter(authErrorExchange);
        }
    }
}
