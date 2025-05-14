package shop.mtcoding.blog.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.blog._core.util.JwtUtil;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

@Transactional
@AutoConfigureMockMvc // MockMvc 클래스가 IOC에 로드
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
// 메모리에 다띄우고 test 뒤에 설정을 mock 이 아니라 RANDOM_PORT 를 쓰면 랜덤으로 진짜 띄움 디파인 포트는 내가 정해서 띄움
public class UserControllerTest {

    // 이렇게 하면 2번 뜨는거임 ioc 컨테이너에 이미 떠잇음
//    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        // 테스트 시작 전에 실행 할 코드
        System.out.println("setUp");
        User ssar = User.builder().id(1).username("ssar").build();
        accessToken = JwtUtil.create(ssar);
    }

    @AfterEach
    public void tearDown() {
        // 테스트 후 정리할 코드
        System.out.println("tearDown");
    }

    @Test
    public void update_test() throws Exception {
        //given
        Integer userId = 1;
        UserRequest.UpdateDTO reqDTO = new UserRequest.UpdateDTO();
        reqDTO.setPassword("12345");
        reqDTO.setEmail("ssar@gmail.com");

        String requestBody = om.writeValueAsString(reqDTO);
//        System.out.println(requestBody);

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/user", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        //eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

//        //then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.email").value("ssar@gmail.com"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.createdAt")
                .value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+$")));

    }

    @Test
    public void checkUsernameAvailable_test() throws Exception {
        //given
        String username = "ssar";

        String requestBody = om.writeValueAsString(username);
//        System.out.println(requestBody);

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/api/check-username-available/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.available").value(false));

    }

    @Test
    public void login_test() throws Exception {
        //given
        UserRequest.LoginDTO resqDTO = new UserRequest.LoginDTO();
        resqDTO.setUsername("ssar");
        resqDTO.setPassword("1234");

        String requestBody = om.writeValueAsString(resqDTO);
//        System.out.println(requestBody);

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON)
        );

        //eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.accessToken")
                .value(Matchers.matchesPattern("^[^.]+\\.[^.]+\\.[^.]+$")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.refreshToken")
                .value(Matchers.matchesPattern("^[^.]+\\.[^.]+\\.[^.]+$")));
    }


    // 메서드 뒤에 적는다는건 날 때린놈한테 위임
    @Test
    public void join_test() throws Exception {
        //given 가짜 데이터 -> 객체가 아니라 json
        UserRequest.JoinDTO resqDTO = new UserRequest.JoinDTO();
        resqDTO.setUsername("haha");
        resqDTO.setPassword("1234");
        resqDTO.setEmail("haha@gmail.com");

        String requestBody = om.writeValueAsString(resqDTO);
//        System.out.println(requestBody);

        //when 테스트 실행
        // 통신을 하면 당연히 헤더와 바디를 다시 줌 ResultActions안에는 그러면 헤더와 바디가 잇음
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders.post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON)
        );
        //eye 결과 눈으로 확인
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then 결과 코드로 검증
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(4));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.username").value("haha"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body.email").value("haha@gmail.com"));
    }
}