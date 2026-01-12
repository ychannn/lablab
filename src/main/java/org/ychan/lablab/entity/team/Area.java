package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_area")
public class Area extends BaseEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 标题
     */
    private String title;

    /**
     * 顺序
     */
    private int sort;
    
}
