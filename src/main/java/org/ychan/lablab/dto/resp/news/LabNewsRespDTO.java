package org.ychan.lablab.dto.resp.news;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.ychan.lablab.entity.news.LabNews;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LabNewsRespDTO {

    private Integer id;
    private String title;
    private String content;
    private LocalDate time;

    /**
     * 封面图 URL（单张）
     */
    private String imageUrl;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static LabNewsRespDTO from(LabNews labNews) {
        LabNewsRespDTO respDTO = new LabNewsRespDTO();
        BeanUtils.copyProperties(labNews, respDTO);
        String img = labNews.getImages();
        if (img != null && !img.isBlank()) {
            if (img.trim().startsWith("[")) {
                try {
                    List<String> arr = JSON.parseArray(img, String.class);
                    respDTO.setImageUrl(arr != null && !arr.isEmpty() ? arr.get(0) : null);
                } catch (Exception e) {
                    respDTO.setImageUrl(null);
                }
            } else {
                respDTO.setImageUrl(img.trim());
            }
        } else {
            respDTO.setImageUrl(null);
        }
        return respDTO;
    }
}


