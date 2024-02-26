package com.example.demo.user.controller.response;

import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private UserStatus status;
    private Long lastLoginAt;

    public static UserResponse from(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();

//        UserResponse userResponse = new UserResponse();
//        userResponse.setId(userEntity.getId());
//        userResponse.setEmail(userEntity.getEmail());
//        userResponse.setNickname(userEntity.getNickname());
//        userResponse.setStatus(userEntity.getStatus());
//        userResponse.setLastLoginAt(userEntity.getLastLoginAt());
//        return userResponse;
    }
}
