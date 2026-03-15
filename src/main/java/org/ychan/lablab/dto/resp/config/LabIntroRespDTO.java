package org.ychan.lablab.dto.resp.config;

import lombok.Data;

import java.util.List;

/**
 * 实验室介绍响应DTO
 */
@Data
public class LabIntroRespDTO {

    /**
     * 实验室名称
     */
    private String labName;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 实验室Logo
     */
    private String logo;

    /**
     * 实验室简介
     */
    private String introduction;

    /**
     * 研究方向列表
     */
    private List<String> researchAreas;

    /**
     * 负责人姓名
     */
    private String leaderName;

    /**
     * 负责人照片
     */
    private String leaderPhoto;

    /**
     * 负责人简介
     */
    private String leaderIntroduction;

    /**
     * 成立时间
     */
    private String establishedDate;

    /**
     * 实验室图片列表
     */
    private List<String> photos;

    /**
     * 荣誉资质
     */
    private List<String> honors;
}
