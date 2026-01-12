package org.ychan.lablab.entity.science;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

import java.time.LocalDateTime;

@Data
@TableName("t_paper_publication")
public class PaperPublication extends BaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 发表时间
     */
    private LocalDateTime publishTime;

    /**
     * 内容
     */
    private String content;
}
