package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_scholar_project")
public class ScholarProject {
    /**
     * 主键id
     */
    private int id;

    /**
     * 学者id
     */
    private int scholarId;

    /**
     * 科研项目id
     */
    private int projectId;
}
