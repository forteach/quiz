package com.forteach.quiz.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;

import static com.forteach.quiz.common.Dic.TokenValidityTime;
import static com.forteach.quiz.common.Dic.WX_USER_PREFIX;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/2/17 17:27
 * @Version: 1.0
 * @Description:
 */
@Service(value = "TokenService")
public class TokenService {

    @Value("${token.salt}")
    private String salt;

    @Resource
    private ReactiveHashOperations<String, String, String> reactiveHashOperations;

    public String createToken(String openId) {
        return JWT.create()
                .withAudience(openId)
                .withExpiresAt(new Date(System.currentTimeMillis() + TokenValidityTime * 1000))
                .sign(Algorithm.HMAC256(salt.concat(openId)));
    }

    public JWTVerifier verifier(String openId) {
        return JWT.require(Algorithm.HMAC256(salt.concat(openId))).build();
    }

//    public String getOpenId(ServerHttpRequest request) {
//        String token = request.
//        return JWT.decode(token).getAudience().get(0);
//    }

    public Mono<String> getSessionKey(String openId) {
        return reactiveHashOperations.get(WX_USER_PREFIX.concat(openId), "sessionKey");
    }
}
