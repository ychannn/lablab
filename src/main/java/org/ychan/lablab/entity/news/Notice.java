package org.ychan.lablab.entity.news;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

import java.time.LocalDateTime;

@Data
@TableName("t_notice")
public class Notice extends BaseEntity {
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
     * 内容
     */
    private String content;

    /**
     * 时间
     */
    private LocalDateTime time;
}

