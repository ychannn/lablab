package org.ychan.lablab.entity.science;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

@Data
@TableName("t_achievement")
public class Achievement extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 内容
     */
    private String content;
}

