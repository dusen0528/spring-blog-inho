package com.nhnacademy.blog.post.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.blogmember.repository.impl.JdbcBlogMemberMappingRepository;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.common.exception.UnauthorizedException;
import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostCreateRequest;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchParam;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;
import com.nhnacademy.blog.post.repository.PostRepository;
import com.nhnacademy.blog.post.repository.impl.JdbcPostRepository;
import com.nhnacademy.blog.post.service.PostService;

import java.util.Objects;
import java.util.Optional;

@Service(PostServiceImpl.BEAN_NAME)
public class PostServiceImpl implements PostService {
    public final static String BEAN_NAME="postServiceImpl";

    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final BlogMemberMappingRepository blogMemberMappingRepository;

    public PostServiceImpl(
            @Qualifier(JdbcPostRepository.BEAN_NAME) PostRepository postRepository,
            @Qualifier(JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository,
            @Qualifier(JdbcBlogMemberMappingRepository.BEAN_NAME) BlogMemberMappingRepository blogMemberMappingRepository
    ) {
        this.postRepository = postRepository;
        this.blogRepository = blogRepository;
        this.blogMemberMappingRepository = blogMemberMappingRepository;
    }

    @Override
    public PostResponse createPost(PostCreateRequest postCreateRequest) {
        //로그인 유무 체크
        checkLoggedin();

        //블로그 존재유무 체크
        checkExistBlog(postCreateRequest.getBlogId());

        //글 작성권한 체크
        Long mbNo = MemberThreadLocal.getMemberNo();
        checkOwnerOrMember(postCreateRequest.getBlogId(), mbNo);

        Post post = Post.ofNewPost(
                postCreateRequest.getBlogId(),
                mbNo,postCreateRequest.getPostTitle(),
                postCreateRequest.getPostContent(),
                postCreateRequest.isPostIsPublic()
        );

        postRepository.save(post);

        return getPost(post.getPostId());
    }

    @Override
    public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
        //로그인 유무 체크
        checkLoggedin();

        //블로그 존재유무 체크
        checkExistBlog(postUpdateRequest.getBlogId());

        //post 존재하는지 체크
        checkExistPost(postUpdateRequest.getPostId());

        //post 수정권한 체크 ( 등록한 사람만 수정할 수 있음)
        long mbNo = MemberThreadLocal.getMemberNo();
        checkOwnerOrMember(postUpdateRequest.getBlogId(), mbNo);

        postRepository.update(postUpdateRequest);

        Optional<Post> postOptional = postRepository.findByPostId(postUpdateRequest.getPostId());
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            // postResponse 형태로 가져올 수 있는 한방 쿼리가 있어야함.

//            PostResponse postResponse = new PostResponse(
//                    post.getPostId(),
//                    post.getBlogId(),
//
//            );

        }
        return null;
    }

    @Override
    public void deletePost(Long postId) {

        //로그인 유무 체크
        checkLoggedin();

        //post 존재하는지 체크
        checkExistPost(postId);

        //post를 삭제할 수 있는 권한이 있는지 체크
        checkUpdateOrDeletePost(postId);

        //post 삭제
        postRepository.deleteByPostId(postId);
    }

    @Override
    public PostResponse getPost(Long postId) {
        //post 조회

        //post 존재하는지 체크
        checkExistPost(postId);

        //post 공개여부 체크

        return null;
    }

    @Override
    public Page<PostResponse> getPostList(Pageable pageable, PostSearchParam postSearchParam) {
        //post 리스트
        // 관리자 또는 ROLE_OWNER or ROLE_MEMBER  비공개 글을 조회할 수 있음

        return null;
    }

    private void checkExistBlog(long blogId) {
        boolean existBlog = blogRepository.existByBlogId(blogId);
        if(!existBlog) {
            throw new BadRequestException("blog [%d] does not exist".formatted(blogId));
        }
    }

    private void checkLoggedin(){
        Long memberNo = MemberThreadLocal.getMemberNo();
        if(Objects.isNull(memberNo)) {
            throw new UnauthorizedException();
        }
    }

    private void checkOwnerOrMember(long blogId, long mbNo) {
        Optional<BlogMemberMapping> blogMemberMappingOptional =  blogMemberMappingRepository.findByMbNoAndBlogId(mbNo, blogId);
        //블로그의 맴버가 아니라면. ForbiddenException() 발생
        if(blogMemberMappingOptional.isEmpty()) {
            throw new ForbiddenException();
        }

        String roleId = blogMemberMappingOptional.get().getRoleId();
        if(!(roleId.equals("ROLE_OWNER") || roleId.equals("ROLE_MEMBER"))) {
            throw new ForbiddenException();
        }
    }

    private void checkExistPost(long postId) {
        boolean existPost = postRepository.existsByPostId(postId);
        if(!existPost) {
            throw new NotFoundException("post [%d] does not exist".formatted(postId));
        }
    }

    private void checkUpdateOrDeletePost(Long postId) {
        Long mbNo = MemberThreadLocal.getMemberNo();
        Optional<Post> postOptional = postRepository.findByPostId(postId);
        if(postOptional.isEmpty()) {
            throw new NotFoundException("post [%d] does not exist".formatted(postId));
        }

        Post post = postOptional.get();
        if(!post.getCreatedMbNo().equals(mbNo)) {
            throw new ForbiddenException();
        }
    }

}
