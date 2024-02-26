package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(long id) {
//        return userJpaRepository.findById(id);

//        return userJpaRepository.findById(id).map(userEntity -> userEntity.toModel()); // userEntity를 도메인 객체로 바꿔주는 메서드 toModel 만들기
        return userJpaRepository.findById(id).map(UserEntity::toModel); // 위와 동일한데 메서드 참조 방식
    }
    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return userJpaRepository.findByIdAndStatus(id, userStatus).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return userJpaRepository.findByEmailAndStatus(email, userStatus).map(UserEntity::toModel);
    }

    @Override
    // 저장하는 부분은 도메인 객체를 영속성 객체로 바꿔주는 메서드 필요
    // 도메인은 인프라 레이어의 정보를 모르는 것이 좋음
    // 따라서 영속성 객체 UserEntity에서 모델을 받아서 fromModel 사용해서 변환
    public User save(User user) {
        return userJpaRepository.save(UserEntity.fromModel(user)).toModel();
    }


}
