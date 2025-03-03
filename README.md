## 핵심 도메인 구조
- member: 회원 관리
- blog: 블로그 정보
- category: 카테고리 관리
- topic: 주제/게시글 관리
- role: 사용자 권한 관리

## 주요 기능별 구조 
**회원 관리 (member)**
- 회원 가입/로그인
- 회원 정보 관리
  
**권한 관리**
- 블로그 관리 (blog)
- 블로그 생성/수정
- 블로그 멤버 관리 (BlogMemberMapping)

**카테고리 관리**
- 게시글 관리 (topic)
- 게시글 작성/수정/삭제
- 카테고리별 게시글 관리

**보안 관련 구조**
- common/security: Spring Security 설정
- common/filter: 인증/인가 필터
- common/exception: 예외 처리

**웹 계층 구조**
- web/controller: MVC 컨트롤러
- templates: Thymeleaf 템플릿
- static: 정적 리소스
