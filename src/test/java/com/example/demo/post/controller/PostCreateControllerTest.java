package com.example.demo.post.controller;

import com.example.demo.post.domain.PostCreate;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MVC 컨트롤러 테스트 위한 인스턴스 자동 구성
@AutoConfigureTestDatabase // 테스트용 데이터베이스 자동 구성
@SqlGroup({
        @Sql(value = "/sql/post-create-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PostCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_게시물을_작성할_수있다() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("hello~~~sanchou")
                .build();

        // when
        // then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        // jackson으로 serialize 해서 컨텐츠 넘겨주게 (그러기 위해 ObjectMapper 만듬)
                        .content(objectMapper.writeValueAsString(postCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("hello~~~sanchou"))
                .andExpect(jsonPath("$.writer.id").isNumber())
                .andExpect(jsonPath("$.writer.email").value("abc@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("bora"));
    }


}
