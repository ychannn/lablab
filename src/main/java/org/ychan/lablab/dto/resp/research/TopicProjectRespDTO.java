package org.ychan.lablab.dto.resp.research;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicProjectRespDTO {

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}

