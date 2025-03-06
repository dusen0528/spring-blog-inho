package com.nhnacademy.blog.bloginfo.dto;


import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.category.domain.Category;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@RequiredArgsConstructor
public class BlogRequest {
    String blogFid;
    boolean blogMain;
    String blogName;
    String blogMbNickName;
    String blogDescription;
}
