package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface BlogRepository {
    void save(Blog blog);
    void update(BlogUpdateRequest blogUpdateRequest);
    void deleteByBlogId(long blogId);
    Optional<Blog> findByBlogId(long blogId);
    boolean existByBlogId(long blogId);
    boolean existByBlogFid(String blogFid);
    boolean existMainBlogByMbNo(long mbNo);

    //blogId에 해당되는 blog들의 공개여부를 설정, true-공개, false-비공개
    void updateByBlogIsPublic(long blogId, boolean blogIsPublic);

    //블로그의 isBlogMain 설정된 값으로 변경 합니다.
    void updateBlogMain(long blogId, boolean isBlogMain);

    //회원이 소속/소유한 (ROLE_OWNER-소유자, ROLE_MEMBER-회원) blog 수
    long countByMbNo(long mbNo, String roleId);

    //회원이 소유및 소속된 모든 blog를 조회 합니다. roleId == ROLE_OWNER 소유하고 있는 blog , roleId==ROLE_MEMBER 팀원으로 소속된 모든 블로그
    List<BlogResponse> findAllBlogs(long mbNo, String roleId);

}