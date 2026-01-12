package org.ychan.lablab.dto.resp.news;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeRespDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}


