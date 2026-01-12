package org.ychan.lablab.dto.req;

import lombok.Data;

@Data
public class ResearchDirectionAddReqDTO {

    /**
     * 研究方向标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
}

