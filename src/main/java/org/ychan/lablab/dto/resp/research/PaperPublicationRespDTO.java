package org.ychan.lablab.dto.resp.research;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.ychan.lablab.entity.science.PaperPublication;

import java.time.LocalDateTime;

@Data
public class PaperPublicationRespDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 发表时间
     */
    private LocalDateTime publishTime;

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
    public static PaperPublicationRespDTO from(PaperPublication paperPublication) {
        PaperPublicationRespDTO respDTO = new PaperPublicationRespDTO();
        BeanUtils.copyProperties(paperPublication, respDTO);
        return respDTO;
    }
}

