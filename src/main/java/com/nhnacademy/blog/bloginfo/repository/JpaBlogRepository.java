package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaBlogRepository extends JpaRepository<Blog, Long> {
    @Query(value = """
            SELECT 
                        1
                    FROM members a
                        INNER JOIN blog_member_mappings b ON a.mb_no = b.mb_no
                        INNER JOIN blogs c ON b.blog_id = c.blog_id
                    WHERE 
                             a.mb_no= :mb_no
                         and c.blog_main=1
                         and b.role_id='ROLE_OWNER'
        """, nativeQuery = true)
    boolean existMainBlogByMbNo(Long mbNo);

    boolean existsByBlogFid(String blogFid);

    @Query(value = """
        select
                            b.blog_id,
                            b.blog_fid,
                            b.blog_main,
                            b.blog_name,
                            b.blog_mb_nickname,
                            b.blog_description,
                            b.blog_is_public,
                            b.created_at,
                            b.updated_at
                        from
                            blog_member_mappings a
                            left join blogs b on a.blog_id = b.blog_id
                        where
                                a.blog_id=b.blog_id
                            and a.mb_no=:mbNo
                            and a.role_id=:roleId
        """, nativeQuery = true)
    List<Blog> findAllBlogs(long mbNo, String roleId);
}
