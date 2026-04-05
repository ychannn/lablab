package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaperPublicationAddReqDTO {

    /**
     * 发表时间
     */
    private LocalDate publishTime;

    /**
     * 内容
     */
    private String content;
}

