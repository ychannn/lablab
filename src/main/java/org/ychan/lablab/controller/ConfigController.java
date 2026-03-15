package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;
import org.ychan.lablab.service.ConfigService;

/**
 * 系统配置 Controller
 * 提供联系我们、实验室介绍等配置信息的查询和更新接口
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    private final ConfigService configService;

    /**
     * 获取联系我们信息（前台接口）
     */
    @GetMapping("/contact")
    public Result<ContactRespDTO> getContactInfo() {
        ContactRespDTO contactInfo = configService.getContactInfo();
        return Result.success(contactInfo);
    }

    /**
     * 获取实验室介绍（前台接口）
     */
    @GetMapping("/lab-intro")
    public Result<LabIntroRespDTO> getLabIntro() {
        LabIntroRespDTO labIntro = configService.getLabIntro();
        return Result.success(labIntro);
    }

    /**
     * 更新联系我们信息（后台管理接口）
     */
    @PutMapping("/admin/contact")
    public Result<Void> updateContactInfo(@RequestBody ContactRespDTO contactDTO) {
        configService.updateContactInfo(contactDTO);
        return Result.success();
    }

    /**
     * 更新实验室介绍（后台管理接口）
     */
    @PutMapping("/admin/lab-intro")
    public Result<Void> updateLabIntro(@RequestBody LabIntroRespDTO introDTO) {
        configService.updateLabIntro(introDTO);
        return Result.success();
    }
}
