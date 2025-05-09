package shop.mtcoding.blog.temp;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class HashTest {

    @Test
    public void encode_test() {
        // $2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii
        // $2a$10$9vPDd3KxKkv/EsOzjS92Y.JTCiUk0dFmG6toDpYbKM3T7wPvtHYIy
        String password = "1234";

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(encPassword);
    }

    @Test
    public void decode_test() {
        // $2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii
        // $2a$10$9vPDd3KxKkv/EsOzjS92Y.JTCiUk0dFmG6toDpYbKM3T7wPvtHYIy
        String dbPassword = "$2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii";
        String password = "1234";
        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        if (dbPassword.equals(encPassword)) {
            System.out.println("비밀번호가 같아요.");
        } else {
            System.out.println("비밀번호가 달라요.");
        }
    }

    @Test
    public void decodeV2_test() {
        // $2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii
        // $2a$10$9vPDd3KxKkv/EsOzjS92Y.JTCiUk0dFmG6toDpYbKM3T7wPvtHYIy
        String dbPassword = "$2a$10$s2Vg6iwT0StC6z946OB6jOi7OGWTP9oBqgP5HOGufwbnFll5Xl4ii";
        String password = "1234";

        Boolean isSame = BCrypt.checkpw(password, dbPassword);
        System.out.println(isSame);
    }
}