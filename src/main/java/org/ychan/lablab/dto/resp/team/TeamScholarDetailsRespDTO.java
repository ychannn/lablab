package org.ychan.lablab.dto.resp.team;

import lombok.Data;
import org.ychan.lablab.entity.team.Intro;
import org.ychan.lablab.entity.team.Paper;
import org.ychan.lablab.entity.team.Project;

import java.util.List;

@Data
public class TeamScholarDetailsRespDTO {
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
     * 简介
     */
    private List<Intro> intro;

    /**
     * 论文
     */
    private List<Paper> paperList;

    /**
     * 科研项目
     */
    private List<Project> projectList;

    /**
     * 照片
     */
    private String photo;

    /**
     * 邮箱
     */
    private String email;


}
