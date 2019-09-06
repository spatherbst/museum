package edu.nwu.museum.common.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JWTUtil {
  // 过期时间五分钟
  private static final long EXPIRE_TIME = 5*60*1000;

  /**
   * 校验 token是否正确
   * @param token  密钥
   * @param secret 用户的密码
   * @return 是否正确
   */
  public static boolean verify(String token, String userId, String secret) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm)
          .withClaim("userId", userId).build();
      verifier.verify(token);
      log.info("token is valid");
      return true;
    } catch (Exception e) {
      log.info("token is invalid{}", e.getMessage());
      return false;
    }
  }

  /**
   * 从 token中获取用户 id
   * @return token中包含的 id
   */
  public static String getUserId(String token) {
    try {
      DecodedJWT jwt = JWT.decode(token);
      return jwt.getClaim("userId").asString();
    } catch (JWTDecodeException e) {
      log.error("error：{}", e.getMessage());
      return null;
    }
  }

  /**
   * 生成 token，暂定 5min 后过期
   * @param userId 用户 id
   * @param secret 用户的密码
   * @return token
   */
  public static String sign(String userId, String secret) {
    try {
      userId = StringUtils.lowerCase(userId);
      Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withClaim("userId", userId)
          .withExpiresAt(date)
          .sign(algorithm);
    } catch (Exception e) {
      log.error("error：{}", e);
      return null;
    }
  }
}