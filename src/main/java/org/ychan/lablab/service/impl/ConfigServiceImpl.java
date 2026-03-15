package org.ychan.lablab.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;
import org.ychan.lablab.entity.config.Config;
import org.ychan.lablab.mapper.ConfigMapper;
import org.ychan.lablab.service.ConfigService;

/**
 * 系统配置 Service 实现
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    private static final String TYPE_CONTACT = "contact";
    private static final String TYPE_LAB_INTRO = "lab_intro";

    @Override
    public ContactRespDTO getContactInfo() {
        Config config = configMapper.selectByType(TYPE_CONTACT);
        if (config == null || config.getConfigValue() == null) {
            return getDefaultContactInfo();
        }
        return JSON.parseObject(config.getConfigValue(), ContactRespDTO.class);
    }

    @Override
    public LabIntroRespDTO getLabIntro() {
        Config config = configMapper.selectByType(TYPE_LAB_INTRO);
        if (config == null || config.getConfigValue() == null) {
            return getDefaultLabIntro();
        }
        return JSON.parseObject(config.getConfigValue(), LabIntroRespDTO.class);
    }

    @Override
    public void updateContactInfo(ContactRespDTO contactDTO) {
        Config config = configMapper.selectByType(TYPE_CONTACT);
        if (config == null) {
            config = new Config();
            config.setConfigType(TYPE_CONTACT);
            config.setConfigName("联系我们");
            config.setDescription("实验室联系信息");
            config.setSort(1);
        }
        config.setConfigValue(JSON.toJSONString(contactDTO));
        
        if (config.getId() == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
    }

    @Override
    public void updateLabIntro(LabIntroRespDTO introDTO) {
        Config config = configMapper.selectByType(TYPE_LAB_INTRO);
        if (config == null) {
            config = new Config();
            config.setConfigType(TYPE_LAB_INTRO);
            config.setConfigName("实验室介绍");
            config.setDescription("实验室基本信息介绍");
            config.setSort(2);
        }
        config.setConfigValue(JSON.toJSONString(introDTO));
        
        if (config.getId() == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
    }

    /**
     * 获取默认联系我们信息
     */
    private ContactRespDTO getDefaultContactInfo() {
        ContactRespDTO dto = new ContactRespDTO();
        dto.setLabName("XX实验室");
        dto.setAddress("XX省XX市XX区XX路XX号");
        dto.setZipCode("100000");
        dto.setPhone("010-12345678");
        dto.setEmail("lab@example.com");
        dto.setWorkTime("周一至周五 9:00-17:00");
        dto.setMapZoom(15);
        dto.setIntroduction("欢迎联系我们，我们将竭诚为您服务。");
        return dto;
    }

    /**
     * 获取默认实验室介绍
     */
    private LabIntroRespDTO getDefaultLabIntro() {
        LabIntroRespDTO dto = new LabIntroRespDTO();
        dto.setLabName("XX实验室");
        dto.setEnglishName("XX Laboratory");
        dto.setIntroduction("本实验室致力于前沿科学研究，拥有优秀的研究团队和先进的实验设备。");
        dto.setLeaderName("XXX教授");
        dto.setLeaderIntroduction("XXX教授是实验室负责人，在相关领域有深厚的研究积累。");
        dto.setEstablishedDate("2020年");
        return dto;
    }
}
