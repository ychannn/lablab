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


}
