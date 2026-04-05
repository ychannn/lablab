package org.ychan.lablab.entity.science;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

import java.time.LocalDate;

@Data
@TableName("t_topic_project")
public class TopicProject extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 内容
     */
    private String content;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;
}

