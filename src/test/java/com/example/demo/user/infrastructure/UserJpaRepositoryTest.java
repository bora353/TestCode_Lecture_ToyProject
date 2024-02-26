package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class) // 내장
@TestPropertySource("classpath:test-application.properties") // 기본설정 따라도 돼서 불필요
@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql") //
public class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

//    @Test
//    void UserRepository_가_제대로_연결되었다(){
//        // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("abc@abc.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("bora");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("sdfdsfsdfw-dsnfkldsjflk-sdjklf");
//
//        // when
//        UserEntity result = userRepository.save(userEntity);
//
//        // then
//        assertThat(result.getId()).isNotNull();
//    }

    @Test
    void findByIdAndStatus로_유저_데이터를_찾아올_수_있다(){
        // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setId(1L);
//        userEntity.setEmail("abc@abc.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("bora");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("sdfdsfsdfw-dsnfkldsjflk-sdjklf");

        // when
//        userRepository.save(userEntity);
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus는_데이터가_없으면_Optional_empty를_내려준다(){
        // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setId(1L);
//        userEntity.setEmail("abc@abc.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("bora");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("sdfdsfsdfw-dsnfkldsjflk-sdjklf");

        // when
//        userRepository.save(userEntity);
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1, UserStatus.PENDING); // 없음!

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}
