package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
//@Sql("/sql/user-service-test-data.sql")
// 테스트 실행 전 데이터 넣는 코드와 실행 후 데이터 정리하는 코드 분리하여 사용
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다(){
        // given
        String email = "abc@naver.com";

        // when
        User result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("bora");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다(){
        // given
        String email = "def@naver.com";

        // when

        // then
        assertThatThrownBy(() -> {
            userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올_수_있다(){
        // given
        // when
        User result = userService.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("bora");
    }

    @Test
    void getById는_PENDING_상태인_유저를_찾아올_수_없다(){
        // given
        // when

        // then
        assertThatThrownBy(() -> {
            userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreateDto를_이용해서_유저를_생성할_수_있다(){
        // given
        UserCreate userCreateDto = UserCreate.builder()
                .email("sanchou@naver.com")
                .nickname("babo")
                .address("dontan")
                .build();

        // SimpleMailMessage를 사용하는 send가 호출되어도 아무 것도 하지 마라
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // when
        User result = userService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        //assertThat(result.getCertificationCode()).isEqualTo("ㅠㅠ");
    }

    @Test
    void userUpdateDto를_이용해서_유저를_생성할_수_있다(){
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("wow")
                .address("moladong")
                .build();

        // when
        userService.update(1, userUpdate);

        // then
        User user = userService.getById(1);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getAddress()).isEqualTo("moladong");
        assertThat(user.getNickname()).isEqualTo("wow");
        //assertThat(result.getCertificationCode()).isEqualTo("ㅠㅠ");
    }

    @Test
    void user를_login_시키면_마지막_로그인_시간이_변경된다(){
        // given

        // when
        userService.login(1);

        // then
        User user = userService.getById(1);
        assertThat(user.getLastLoginAt()).isGreaterThan(0);
        //assertThat(result.getLastLoginAt()).isEqualTo("ㅠㅠ");
    }

    @Test
    void PENDING_상태의_사용자는_인증코드로_ACTIVE_시킬_수_있다(){
        // given

        // when
        userService.verifyEmail(2, "aaa-bbb-ccc-ddd");

        // then
        User user = userService.getById(2);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증코드를_받으면_에러를_던진다(){
        // given
        // when

        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "kldsjflksdfjkdls");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
