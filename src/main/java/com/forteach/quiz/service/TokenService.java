package com.forteach.quiz.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.forteach.quiz.exceptions.TokenException;
import com.forteach.quiz.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import static com.forteach.quiz.common.Dic.USER_PREFIX;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/2/17 17:27
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@Service(value = "TokenService")
public class TokenService {

    @Value("${token.salt}")
    private String salt;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private JWTVerifier verifier(String openId) {
        return JWT.require(Algorithm.HMAC256(salt.concat(openId))).build();
    }

    /**
     * 通过token 获取openId
     * @param request
     * @return
     */
    private String getOpenId(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("token");
        Assert.notNull(token, "token is null");
        return JWT.decode(token).getAudience().get(0);
    }

    /**
     * 根据token 查找对应的学生信息
     * @param request
     * @return
     */
    public String getStudentId(ServerHttpRequest request){
        return  String.valueOf(stringRedisTemplate.opsForHash().get(USER_PREFIX.concat(getOpenId(request)), "studentId"));
    }

    /**
     * 校验token 是否有效
     * @param request
     * @return
     */
    public Mono<Boolean> check(ServerRequest request){
        String token = request.exchange().getRequest().getHeaders().getFirst("token");
        if (StringUtil.isEmpty(token)){
            log.error("token is null");
            return Mono.error(new TokenException("缺少token"));
        }
        try {
            String openId = JWT.decode(token).getAudience().get(0);
            verifier(openId).verify(token);
        } catch (JWTVerificationException e) {
            log.error("token check false 401");
            return Mono.error(new TokenException("401"));
        }
        return Mono.just(true);
    }
}
