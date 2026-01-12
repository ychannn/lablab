package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_project")
public class Project extends BaseEntity {

    /**
     * 主键id
     */
    private int id;

    /**
     * 学者id
     */
    private int scholarId;

    /**
     * 内容
     */
    private String content;
}
