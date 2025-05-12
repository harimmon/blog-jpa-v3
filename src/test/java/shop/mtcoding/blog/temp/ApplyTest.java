package shop.mtcoding.blog.temp;

import org.junit.jupiter.api.Test;
import shop.mtcoding.blog.Apply;
import shop.mtcoding.blog.ApplyEnum;

public class ApplyTest {

    @Test
    public void create_test() {
        System.out.println(ApplyEnum.Pass.value);
        System.out.println(ApplyEnum.Fail.value);

        Apply apply = new Apply(1, "홍길동", 3, ApplyEnum.Pass);
        System.out.println(apply.getStatus());
    }
}
