package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学者论文关系
 */
@Data
@TableName("t_scholar_paper")
public class ScholarPaper {

    /***
     * 主键id
     */
    private int id;

    /**
     * 学者id
     */
    private int scholarId;

    /**
     * 论文id
     */
    private int paperId;
}
