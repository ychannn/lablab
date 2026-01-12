package org.ychan.lablab.common.result;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 响应状态码：
     * 200 - 成功
     * 400 - 参数错误/请求错误
     * 404 - 资源不存在
     * 500 - 系统内部错误
     */
    private int code;

    private String message;

    private PageData<T> pageData;

    @Data
    public static class PageData<T>{
        private long total;

        private int pageNum;

        private int pageSize;

        private List<T> list;
    }
    private PageResult(int code, String message, PageData<T> pageData){
        this.code = code;
        this.message = message;
        this.pageData = pageData;
    }

    public static <T> PageResult<T> success(int code, String message, List<T> list){
        PageInfo<T> pageInfo = new PageInfo<>(list);
        PageData<T> pageData = new PageData<>();
        pageData.setTotal(pageInfo.getTotal());
        pageData.setPageNum(pageInfo.getPageNum());
        pageData.setPageSize(pageInfo.getPageSize());
        pageData.setList(list);
        return new PageResult<>(code, message, pageData);
    }

    public static <T> PageResult<T> success(List<T> list){
        return PageResult.success(200, "操作成功", list);
    }
}
