package org.ychan.lablab.dto.resp.team;

import lombok.Data;

@Data
public class TeamBasicScholarRespDTO {
    /**
     * 主键id
     */
    private int id;

    /**
     * 领域 id
     */
    private int area;

    /**
     * 领域名称
     */
    private String areaName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 级别（数字，1=教授 2=副教授 3=讲师 4=助教）
     */
    private int rank;

    /**
     * 职级名称（教授/副教授/讲师/助教）
     */
    private String rankLabel;

    /**
     * 照片
     */
    private String photo;
}
