package com.nhnacademy.blog.common.websupport.pageable;

/**
 * 페이징 요청 정보를 나타내는 인터페이스입니다.
 */
public interface Pageable {

    /**
     * 현재 페이지 번호를 반환합니다.
     *
     * @return 페이지 번호
     */
    int getPageNumber();

    /**
     * 현재 페이지의 크기를 반환합니다.
     *
     * @return 페이지 크기
     */
    int getPageSize();

    /**
     * 페이지 오프셋을 계산하여 반환합니다.
     *
     * @return 페이지 오프셋
     */
    long getOffset();

    /**
     * 다음 페이지 요청을 생성하여 반환합니다.
     *
     * @return 다음 페이지 요청
     */
    Pageable next();

    /**
     * 이전 페이지 또는 첫 페이지 요청을 생성하여 반환합니다.
     *
     * @return 이전 페이지 또는 첫 페이지 요청
     */
    Pageable previousOrFirst();

    /**
     * 첫 페이지 요청을 생성하여 반환합니다.
     *
     * @return 첫 페이지 요청
     */
    Pageable first();

    /**
     * 이전 페이지가 있는지 여부를 반환합니다.
     *
     * @return 이전 페이지 존재 여부
     */
    boolean hasPrevious();
}
