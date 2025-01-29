package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.domain.QBlog;
import com.nhnacademy.blog.bloginfo.repository.CustomBlogRepository;
import com.nhnacademy.blog.blogmember.domain.QBlogMemberMapping;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class CustomBlogRepositoryImpl extends QuerydslRepositorySupport implements CustomBlogRepository {

    public CustomBlogRepositoryImpl() {
        super(Blog.class);
    }

    QBlog qBlog = QBlog.blog;
    QBlogMemberMapping qBlogMemberMapping = QBlogMemberMapping.blogMemberMapping;


    @Override
    public String blogFidFromMainBlog(Long mbNo) {

        JPAQuery<String> query =new JPAQuery(getEntityManager());
        query.from(qBlogMemberMapping)
                .join(qBlog).on(qBlogMemberMapping.blog.eq(qBlog))
                .where(
                        qBlogMemberMapping.role.roleId.eq("ROLE_OWNER")
                                .and(qBlogMemberMapping.member.mbNo.eq(mbNo))
                                .and(qBlog.blogMain.isTrue())
                );

        String blogFid = query
                .select(qBlog.blogFid)
                .fetchOne();

        return blogFid;
    }
}
