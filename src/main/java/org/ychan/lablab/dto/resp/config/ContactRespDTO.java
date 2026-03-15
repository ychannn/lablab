package org.ychan.lablab.dto.resp.config;

import lombok.Data;

/**
 * 联系我们响应DTO
 */
@Data
public class ContactRespDTO {

    /**
     * 实验室名称
     */
    private String labName;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 传真
     */
    private String fax;

    /**
     * 经度（地图用）
     */
    private Double longitude;

    /**
     * 纬度（地图用）
     */
    private Double latitude;

    /**
     * 地图缩放级别
     */
    private Integer mapZoom;

    /**
     * 工作时间
     */
    private String workTime;

    /**
     * 微信公众号
     */
    private String wechat;

    /**
     * 简介
     */
    private String introduction;
}
