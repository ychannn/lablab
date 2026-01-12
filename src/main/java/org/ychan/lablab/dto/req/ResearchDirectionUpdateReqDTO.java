package org.ychan.lablab.dto.req;

import lombok.Data;

@Data
public class ResearchDirectionUpdateReqDTO {

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
}

