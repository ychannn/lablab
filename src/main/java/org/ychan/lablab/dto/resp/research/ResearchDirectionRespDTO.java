package org.ychan.lablab.dto.resp.research;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResearchDirectionRespDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 研究方向标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}

