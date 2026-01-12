package org.ychan.lablab.dto.req;

import lombok.Data;
import org.ychan.lablab.entity.team.Intro;
import org.ychan.lablab.entity.team.Paper;
import org.ychan.lablab.entity.team.Project;

import java.util.List;

@Data
public class TeamAddScholarReqDTO {

    /**
     * 领域
     */
    private int areaId;

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

    /**
     * 简介
     */
    private List<Intro> introList;

    /**
     * 论文
     */
    private List<Paper> paperList;

    /**
     * 科研项目
     */
    private List<Project> projectList;
}
