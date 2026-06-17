package com.javaBean;

import java.util.List;

/**
 * 分页结果封装类
 */
public class PageResult<T> {
    private List<T> data;        // 当前页数据
    private int currentPage;     // 当前页码
    private int pageSize;        // 每页大小
    private int totalCount;      // 总记录数
    private int totalPages;      // 总页数

    public PageResult() {
    }

    public PageResult(List<T> data, int currentPage, int pageSize, int totalCount) {
        this.data = data;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public boolean isLastPage() {
        return currentPage >= totalPages;
    }
}