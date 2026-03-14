package org.ychan.lablab.dto.resp.research;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.ychan.lablab.entity.science.Achievement;

import java.time.LocalDateTime;

@Data
public class AchievementRespDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 从实体类转换为DTO
     */
    public static AchievementRespDTO from(Achievement achievement) {
        AchievementRespDTO respDTO = new AchievementRespDTO();
        BeanUtils.copyProperties(achievement, respDTO);
        return respDTO;
    }
}

