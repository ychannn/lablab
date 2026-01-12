package org.ychan.lablab.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AreaRespDTO {

    /**
     * 主键id
     */
    private int id;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序
     */
    private int sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}

