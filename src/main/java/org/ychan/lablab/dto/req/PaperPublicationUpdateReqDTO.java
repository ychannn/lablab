package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaperPublicationUpdateReqDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 发表时间
     */
    private LocalDateTime publishTime;

    /**
     * 内容
     */
    private String content;
}

