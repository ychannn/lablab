package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaperPublicationAddReqDTO {

    /**
     * 发表时间
     */
    private LocalDateTime publishTime;

    /**
     * 内容
     */
    private String content;
}

