package com.nhnacademy.blog.common.websupport.page;

import java.util.List;

/**
 * 페이징된 데이터의 일부를 나타내는 인터페이스입니다.
 *
 * @param <T> 데이터 타입
 */
public interface Page<T> {

    /**
     * 현재 페이지의 데이터 목록을 반환합니다.
     *
     * @return 데이터 목록
     */
    List<T> getContent();

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
     * 전체 데이터 수를 반환합니다.
     *
     * @return 전체 데이터 수
     */
    long getTotalElements();

    /**
     * 전체 페이지 수를 반환합니다.
     *
     * @return 전체 페이지 수
     */
    int getTotalPages();

    /**
     * 다음 페이지가 있는지 여부를 반환합니다.
     *
     * @return 다음 페이지 존재 여부
     */
    boolean hasNext();

    /**
     * 이전 페이지가 있는지 여부를 반환합니다.
     *
     * @return 이전 페이지 존재 여부
     */
    boolean hasPrevious();

    /**
     * 현재 페이지가 첫 번째 페이지인지 여부를 반환합니다.
     *
     * @return 첫 번째 페이지 여부
     */
    boolean isFirst();

    /**
     * 현재 페이지가 마지막 페이지인지 여부를 반환합니다.
     *
     * @return 마지막 페이지 여부
     */
    boolean isLast();
}

