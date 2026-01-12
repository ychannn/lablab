package org.ychan.lablab.entity.team;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_intro")
public class Intro extends BaseEntity{

    private int id;

    private int scholarId;

    private String content;

}
