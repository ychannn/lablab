package org.ychan.lablab.dto.req;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LabNewsUpdateReqDTO {

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
    private LocalDate time;

    /**
     * 封面图 URL（单张）
     */
    private String imageUrl;
}


