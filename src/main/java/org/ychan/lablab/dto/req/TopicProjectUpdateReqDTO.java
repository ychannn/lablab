package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicProjectUpdateReqDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}

