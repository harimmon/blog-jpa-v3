package shop.mtcoding.blog.integre;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.blog.MyRestDoc;
import shop.mtcoding.blog._core.util.JwtUtil;
import shop.mtcoding.blog.user.User;
import shop.mtcoding.blog.user.UserRequest;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
// 메모리에 다띄우고 test 뒤에 설정을 mock 이 아니라 RANDOM_PORT 를 쓰면 랜덤으로 진짜 띄움 디파인 포트는 내가 정해서 띄움
public class UserControllerTest extends MyRestDoc {

    // 이렇게 하면 2번 뜨는거임 ioc 컨테이너에 이미 떠잇음
//    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ObjectMapper om;

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
    public void join_username_uk_fail_test() throws Exception { // 이 메서드를 호출한 주체에게 예외 위임 -> 지금은 jvm 이다
        // given -> 가짜 데이터
        UserRequest.JoinDTO reqDTO = new UserRequest.JoinDTO();
        reqDTO.setEmail("ssar@nate.com");
        reqDTO.setPassword("1234");
        reqDTO.setUsername("ssar");

        String requestBody = om.writeValueAsString(reqDTO);
//        System.out.println(requestBody); // {"username":"haha","password":"1234","email":"haha@nate.com"}

        // when -> 테스트 실행
        ResultActions actions = mvc.perform( // 주소가 틀리면 터지고, json 아닌거 넣으면 터지고, 타입이 달라도 터지고. 따라서 미리 터진다고 알려줌
                MockMvcRequestBuilders
                        .post("/join")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye -> 결과 눈으로 검증
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        //System.out.println(responseBody); // {"status":200,"msg":"성공","body":{"id":4,"username":"haha","email":"haha@nate.com","createdAt":"2025-05-13 11:45:23.604577"}}

        // then -> 결과를 코드로 검증 // json의 최상위 객체를 $ 표기한다
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("중복된 유저네임이 존재합니다"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").value(Matchers.nullValue()));
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    public void check_username_available_test() throws Exception {
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
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}