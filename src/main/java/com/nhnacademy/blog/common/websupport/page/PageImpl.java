package com.nhnacademy.blog.common.websupport.page;

import java.util.List;

/**
 * Page 인터페이스의 구현 클래스입니다.
 *
 * @param <T> 데이터 타입
 */
public class PageImpl<T> implements Page<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;

    public PageImpl(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    @Override
    public boolean hasNext() {
        return pageNumber < getTotalPages() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    @Override
    public boolean isFirst() {
        return pageNumber == 0;
    }

    @Override
    public boolean isLast() {
        return pageNumber == getTotalPages() - 1;
    }
}
