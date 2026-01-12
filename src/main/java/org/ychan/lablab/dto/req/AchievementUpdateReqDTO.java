package org.ychan.lablab.dto.req;

import lombok.Data;

@Data
public class AchievementUpdateReqDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 内容
     */
    private String content;
}

