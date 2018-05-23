package mvc.token.utils;

import io.jsonwebtoken.*;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenUtils {
    private final Logger logger = Logger.getLogger(TokenUtils.class.getName());

    public static Set<String> filterUris = new HashSet<>();

    //实现单例，饿汉式
    private TokenUtils(){}

    private static TokenUtils instance = new TokenUtils();

    public static TokenUtils tokenUtils() {
        return instance;
    }

    private String secret = "asdasdfa";

    private Long expiration = 100L;

    /**
     * 根据 detail 生成 Token
     *
     * @return
     */
//    public static String getTokenWithDetail(Map<String, Object> detail) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("sub", detail.get("username"));
//        claims.put("created", generateCurrentDate());
//        return generateToken(claims);
//    }


    /**
     * 根据 claims 生成 Token
     *
     * @param claims
     * @return
     */
    public String generateToken(Map<String, Object> claims) {

        claims.put("created", this.generateCurrentDate());

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(this.generateExpirationDate())
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                    .compact();
        } catch (UnsupportedEncodingException e) {
            //didn't want to have this method throw the exception, would rather log it and sign the token like it was before
            //logger.log(Level.WARNING, ex.getMessage());
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(this.generateExpirationDate())
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        }
    }


    /**
     * token 过期时间
     *
     * @return
     */
    private Date generateExpirationDate() {
        Long currentTime = System.currentTimeMillis();
        Long expirationTime = currentTime + expiration * 1000;
        System.out.println("currentTime: " + currentTime + "  currentDate: " + new Date(currentTime));
        System.out.println("expirationTime: " + expirationTime + "   expirationDate: " + new Date(expirationTime));
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }


    /**
     * 获得当前时间
     *
     * @return
     */
    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 从 token 中拿到 username
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = (String) claims.get("username");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 解析 token 的主体 Claims
     *
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {


            if (Jwts.parser().setSigningKey(secret.getBytes("UTF-8")).isSigned(token) == false) {
                throw new IsSignedException("token: " + token + " ,token格式错误！");
            }


            claims = Jwts.parser()
                    .setSigningKey(secret.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("claims: " + claims);

//            System.out.println(
//            Jwts.parser()
//                    .setSigningKey(secret.getBytes("UTF-8"))
//                    .parse(token)
//                    .getBody()
//            );

        } catch (IsSignedException e) {
            logger.log(Level.WARNING, "不是有效的token, " + e.getMessage());
        } catch (SignatureException e) {
            logger.log(Level.WARNING, "token签名不匹配, " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.log(Level.WARNING, "token过期, " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }

        return claims;
    }

    class IsSignedException extends RuntimeException {
        public IsSignedException(String message){super(message);}
    }

    /**
     * 检查 token 是否处于有效期内
     * @param token
     * @return
     */
    public Boolean validateToken(String token) {
//        final String username = this.getUsernameFromToken(token);
//        final Date created = this.getCreatedDateFromToken(token);
        return !(this.isTokenExpired(token));
//        && !(this.isCreatedBeforeLastPasswordReset(created, user.getLastPasswordReset())));
    }

    /**
     * 获得我们封装在 token 中的 token 创建时间
     * @param token
     * @return
     */
    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    /**
     * 获得我们封装在 token 中的 token 过期时间
     * @param token
     * @return
     */
//    public Date getExpirationDateFromToken(String token) {
//        Date expiration = null;
//        try {
//            final Claims claims = this.getClaimsFromToken(token);
//            expiration = claims.getExpiration();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return expiration;
//    }

    /**
     * 检查当前时间是否在封装在 token 中的过期时间之后，若是，则判定为 token 过期
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        Claims claims = this.getClaimsFromToken(token);

        if (claims != null) {
            Date expiration = claims.getExpiration();
            return expiration.after(this.generateCurrentDate());
        } else {
            return true;
        }


    }

    /**
     * 检查 token 是否是在最后一次修改密码之前创建的（账号修改密码之后之前生成的 token 即使没过期也判断为无效）
     * @param created
     * @param lastPasswordReset
     * @return
     */
    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }
}
