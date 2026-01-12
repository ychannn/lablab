package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 */
@Data
@TableName("t_scholar")
public class Scholar extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

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
     * 邮箱
     */
    private String email;
}
