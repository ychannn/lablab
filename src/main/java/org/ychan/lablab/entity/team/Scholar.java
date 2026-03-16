package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_scholar")
public class Scholar extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private int id;

    private int areaId;

    private String name;

    private int title;

    private String photo;

    private String email;
}
