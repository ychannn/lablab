package org.ychan.lablab.entity.science;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

@Data
@TableName("t_research_direction")
public class ResearchDirection extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 研究方向标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


}
