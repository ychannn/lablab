package org.ychan.lablab.entity.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.ychan.lablab.entity.team.BaseEntity;

/**
 * 系统配置表
 * 用于存储各类配置信息，如联系我们、实验室介绍等
 */
@Data
@TableName("t_config")
public class Config extends BaseEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置类型：contact-联系我们, intro-实验室介绍, etc.
     */
    private String configType;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置内容（JSON格式）
     */
    private String configValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;
}
