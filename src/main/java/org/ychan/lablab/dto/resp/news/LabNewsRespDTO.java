package org.ychan.lablab.dto.resp.news;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.ychan.lablab.entity.news.LabNews;

import java.time.LocalDateTime;

@Data
public class LabNewsRespDTO {

    /**
     * 主键id
     */
    private Integer id;

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
    public static LabNewsRespDTO from(LabNews labNews) {
        LabNewsRespDTO respDTO = new LabNewsRespDTO();
        BeanUtils.copyProperties(labNews, respDTO);
        return respDTO;
    }
}


