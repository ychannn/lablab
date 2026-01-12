package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeUpdateReqDTO {

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
}


