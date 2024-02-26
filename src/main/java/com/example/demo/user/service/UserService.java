package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;

import java.time.Clock;
import java.util.UUID;

import com.example.demo.user.infrastructure.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

//    private final UserJpaRepository userJpaRepository;
//    private final JavaMailSender mailSender;
    private final UserRepositoryImpl userRepository;
    private final CertificationService certificationService;

    public User getByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    public User getById(long id) { // get은 throw 던짐
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    public User create(UserCreate userCreate) {
        User user = User.from(userCreate);

//        user.setEmail(userCreate.getEmail());
//        user.setNickname(userCreate.getNickname());
//        user.setAddress(userCreate.getAddress());
//        user.setStatus(UserStatus.PENDING);
//        user.setCertificationCode(UUID.randomUUID().toString());
        user = userRepository.save(user);

        certificationService.send(userCreate.getEmail(), user.getId(), user.getCertificationCode());
        //String certificationUrl = generateCertificationUrl(userEntity);
        //sendCertificationEmail(userCreate.getEmail(), certificationUrl);
        return user;
    }

    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = getById(id);
        user = user.update(userUpdate);
//        userEntity.setNickname(userUpdate.getNickname());
//        userEntity.setAddress(userUpdate.getAddress());
//        userEntity = userRepository.save(user);
        return user;
    }

    @Transactional
    public void login(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user.login();
        userRepository.save(user); // JPA와 의존성 끊어지면서 직접 저장해줘야 함! 위에는 도메인 객체, 여기는 영속성 객체
    }

    @Transactional
    public void verifyEmail(long id, String certificationCode) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user = user.certificate(certificationCode);
        userRepository.save(user);
    }


}