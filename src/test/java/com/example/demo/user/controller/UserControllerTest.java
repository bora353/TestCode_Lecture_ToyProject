package com.example.demo.user.controller;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // MVC 컨트롤러 테스트 위한 인스턴스 자동 구성
@AutoConfigureTestDatabase // 테스트용 데이터베이스 자동 구성
@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달_받을_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("abc@naver.com"))
                .andExpect(jsonPath("$.nickname").value("bora"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
                // 엔드포인트 요청에 대한 응답으로 받은 JSON 필드에서 각 값이 일치하는지 확인
    }

    @Test
    void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/12345678"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 12345678를 찾을 수 없습니다."));
                // 응답이 json 아니고 문자열이면 content 사용
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaa-bbb-ccc-ddd"))
                .andExpect(status().isFound()); // 리다이렉트라 응답없음

        UserEntity userEntity = userJpaRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaaaaaaaa-bbb-ccc-ddd"))
                .andExpect(status().isForbidden()); // 리다이렉트라 응답없음
    }

    @Test
    void 사용자는_내_정보를_불러올때_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL","abc@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("abc@naver.com"))
                .andExpect(jsonPath("$.nickname").value("bora"))
                .andExpect(jsonPath("$.address").value("seoul"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("wow123")
                .address("busan")
                .build();

        // when
        // then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "abc@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        // jackson으로 serialize 해서 컨텐츠 넘겨주게 (그러기 위해 ObjectMapper 만듬)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("abc@naver.com"))
                .andExpect(jsonPath("$.nickname").value("wow123"))
                .andExpect(jsonPath("$.address").value("busan"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

}
