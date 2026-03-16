package org.ychan.lablab.dto.resp.search;

import lombok.Data;

import java.util.List;

/**
 * 搜索结果响应DTO
 */
@Data
public class SearchRespDTO {

    private long total;
    private int page;
    private int size;
    private int totalPages;
    private List<SearchItemDTO> items;

    /**
     * 搜索结果项
     */
    @Data
    public static class SearchItemDTO {
        private String id;
        private String type;
        private String title;
        private String content;
        private String author;
        private String createTime;
        private String url;
        private String imageUrl;
    }
}
