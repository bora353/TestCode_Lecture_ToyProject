package com.example.demo.post.controller.dto;

import com.example.demo.user.controller.dto.UserResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {

    private Long id;
    private String content;
    private Long createdAt;
    private Long modifiedAt;
    private UserResponse writer;
}