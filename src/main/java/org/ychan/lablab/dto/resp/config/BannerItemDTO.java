package org.ychan.lablab.dto.resp.config;

import lombok.Data;

/**
 * 首页轮播图单项（config 存储，后台上传图片后配置）
 */
@Data
public class BannerItemDTO {

    /**
     * 图片地址（相对路径如 /uploads/banner/xx.jpg 或完整 URL）
     */
    private String imageUrl;

    /**
     * 可选标题（门户可做遮罩展示）
     */
    private String title;

    /**
     * 可选跳转链接
     */
    private String link;

    /**
     * 排序，越小越靠前
     */
    private Integer sort;
}
