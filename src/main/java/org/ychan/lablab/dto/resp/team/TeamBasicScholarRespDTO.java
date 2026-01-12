package org.ychan.lablab.dto.resp.team;

import lombok.Data;

@Data
public class TeamBasicScholarRespDTO {
    /**
     * 主键id
     */
    private int id;

    /**
     * 分组
     */
    private int area;

    /**
     * 姓名
     */
    private String name;

    /**
     * 级别
     */
    private int rank;

    /**
     * 照片
     */
    private String photo;
}
