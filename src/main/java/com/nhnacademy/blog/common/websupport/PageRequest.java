package com.nhnacademy.blog.common.websupport;

public class PageRequest {
    private long page;
    private long size;

    public PageRequest(long page, long size) {
        this.page = page;
        this.size = size;
    }

    public long getPage() {
        return page;
    }

    public long getSize() {
        return size;
    }

    public long getOffSet(){
        return (page - 1) * size;
    }

}
