package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页返回结构
 */
@Data
public class PageResult<T> implements Serializable {

    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
