package com.nhnacademy.blog.common.websupport.pageable;

/**
 * Pageable 인터페이스의 구현 클래스
 */
public class PageRequest implements Pageable {

    private final int pageNumber;
    private final int pageSize;

    public PageRequest(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
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
    public long getOffset() {
        return (long) pageNumber * pageSize;
    }

    @Override
    public Pageable next() {
        return new PageRequest(pageNumber + 1, pageSize);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? new PageRequest(pageNumber - 1, pageSize) : this;
    }

    @Override
    public Pageable first() {
        return new PageRequest(0, pageSize);
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }
}
