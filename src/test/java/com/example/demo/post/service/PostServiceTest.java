package com.example.demo.post.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
//@Sql("/sql/user-service-test-data.sql")
// 테스트 실행 전 데이터 넣는 코드와 실행 후 데이터 정리하는 코드 분리하여 사용
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void getById는_존재하는_게시물을_내려준다(){
        // given

        // when
        Post result = postService.getById(1);

        // then
        assertThat(result.getContent()).isEqualTo("hellosanchou");
        assertThat(result.getWriter().getEmail()).isEqualTo("abc@naver.com");
    }

    @Test
    void postCreateDto를_이용해서_게시물을_생성할_수_있다(){
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("yayaya")
                .build();

        // when
        Post result = postService.create(postCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isEqualTo("yayaya");
        assertThat(result.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void postUpdateDto를_이용해서_게시물을_수정할_수_있다(){
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("hello :)")
                .build();

        // when
        postService.update(1, postUpdate);

        // then
        Post post = postService.getById(1);
        assertThat(post.getContent()).isEqualTo("hello :)");
        assertThat(post.getModifiedAt()).isGreaterThan(0);
    }

}
