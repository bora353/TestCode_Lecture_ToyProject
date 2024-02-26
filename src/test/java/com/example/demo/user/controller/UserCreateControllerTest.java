package com.example.demo.user.controller;

import com.example.demo.user.domain.UserCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MVC 컨트롤러 테스트 위한 인스턴스 자동 구성
@AutoConfigureTestDatabase // 테스트용 데이터베이스 자동 구성
@SqlGroup({
    @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_회원가입을_할_수있고_회원가입된_사용자는_PENDING_상태이다() throws Exception {
        // given
        UserCreate userCreateDto = UserCreate.builder()
                .nickname("wow123")
                .address("jeju")
                .email("wow123@naver.com")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        // then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        // jackson으로 serialize 해서 컨텐츠 넘겨주게 (그러기 위해 ObjectMapper 만듬)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("wow123@naver.com"))
                .andExpect(jsonPath("$.nickname").value("wow123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

}
