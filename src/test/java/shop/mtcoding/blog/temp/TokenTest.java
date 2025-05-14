package shop.mtcoding.blog.temp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import shop.mtcoding.blog.user.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class TokenTest {

    @Test
    public void create_test() {
        User user = User.builder()
                .id(1)
                .username("ssar")
                .password("$2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii")
                .email("ssar@nate.com")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        String jwt = JWT.create() // 페이로드에 들어가는 것들
                .withSubject("blogv3") // 아무거나 적어도 됨
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 만료 시간 (현재는 1시간)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC256("metacoding"));

        // 198 156 236 87 42 53 186 254 56 151 169 7 107 178 5 197 147 172 56 100 145 97 133 14 17 46 135 193 73 199 201 144
        // xpzsVyo1uv44l6kHa7IFxZOsOGSRYYUOES6HwUnHyZA
        System.out.println(jwt);
    }

    @Test
    public void verify_test() {
        User user = User.builder()
                .id(1)
                .username("ssar")
                .password("$2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii")
                .email("ssar@nate.com")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        String jwt = JWT.create() // 페이로드에 들어가는 것들
                .withSubject("blogv3") // 아무거나 적어도 됨
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 만료 시간 (현재는 1시간)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC256("metacoding"));

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("metacoding")).build().verify(jwt);
        Integer id = decodedJWT.getClaim("id").asInt();
        String username = decodedJWT.getClaim("username").asString();

        System.out.println(id);
        System.out.println(username);
    }
}
