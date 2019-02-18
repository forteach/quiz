package com.forteach.quiz.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private HashOperations<String, String, String> hashOperations;


    public String createToken(String openId) {
        return JWT.create().withAudience(openId)
                .sign(Algorithm.HMAC256(salt.concat(openId)));
    }

    public JWTVerifier verifier(String openId) {
        return JWT.require(Algorithm.HMAC256(salt.concat(openId))).build();
    }

//    public String getOpenId(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        return JWT.decode(token).getAudience().get(0);
//    }

//    public String getSessionKey(String openId) {
//        return hashOperations.get(WX_USER_PREFIX.concat(openId), "sessionKey");
//    }
}
