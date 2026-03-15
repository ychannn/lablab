package org.ychan.lablab.service;

import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;

/**
 * 系统配置 Service
 */
public interface ConfigService {

    /**
     * 获取联系我们信息
     */
    ContactRespDTO getContactInfo();

    /**
     * 获取实验室介绍
     */
    LabIntroRespDTO getLabIntro();

    /**
     * 更新联系我们信息
     */
    void updateContactInfo(ContactRespDTO contactDTO);

    /**
     * 更新实验室介绍
     */
    void updateLabIntro(LabIntroRespDTO introDTO);
}
