package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogMemberMappingRepository extends JpaRepository<BlogMemberMapping, Long> {
    //method name에 이미 member는 포함되어 있음으로 blog만 추가 해주면 해당 method가 실행 될 때 , member, blog를 함께 join 합니다.
    //이럴때 사용하는것이 @EntityGraph 입니다.
    @EntityGraph(attributePaths = {"blog"})
    List<BlogMemberMapping> findBlogMemberMappingsByMember_MbNo(Long mbNo);

}
