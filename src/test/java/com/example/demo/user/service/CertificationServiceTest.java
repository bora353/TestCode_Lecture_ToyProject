package com.example.demo.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.Test;

public class CertificationServiceTest {

    // 테스트 엄청 빠름...!
    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져서_보내지는지_테스트한다() {
        // given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);

        // when
        certificationService.send("abc@naver.com",1, "aa-bb-cc-dd");

        // then
        assertThat(fakeMailSender.email).isEqualTo("abc@naver.com");
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content).isEqualTo(
                "Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aa-bb-cc-dd");

    }
}
