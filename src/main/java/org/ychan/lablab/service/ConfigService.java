package org.ychan.lablab.service;

import org.ychan.lablab.dto.resp.config.BannerItemDTO;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;

import java.util.List;

/**
 * 系统配置 Service
 */
public interface ConfigService {

    /**
     * 获取网站标题（门户导航、页脚、浏览器标题）
     */
    String getSiteTitle();

    /**
     * 获取联系我们信息
     */
    ContactRespDTO getContactInfo();

    /**
     * 获取实验室介绍
     */
    LabIntroRespDTO getLabIntro();

    /**
     * 获取首页轮播图列表（门户用：按 sort 倒序取前 5 张）
     */
    List<BannerItemDTO> getBannerList();

    /**
     * 获取轮播图完整列表（后台用：用于拖动排序与编辑）
     */
    List<BannerItemDTO> getBannerListForAdmin();

    /**
     * 更新网站标题
     */
    void updateSiteTitle(String title);

    /**
     * 更新联系我们信息
     */
    void updateContactInfo(ContactRespDTO contactDTO);

    /**
     * 更新实验室介绍
     */
    void updateLabIntro(LabIntroRespDTO introDTO);

    /**
     * 更新首页轮播图列表（后台管理）
     */
    void updateBannerList(List<BannerItemDTO> list);
}
