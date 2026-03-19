package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.dto.resp.config.BannerItemDTO;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;
import org.ychan.lablab.service.ConfigService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 系统配置 Controller
 * 提供联系我们、实验室介绍等配置信息的查询和更新接口
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    private final ConfigService configService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 获取网站标题（前台接口）
     */
    @GetMapping("/site-title")
    public Result<String> getSiteTitle() {
        return Result.success(configService.getSiteTitle());
    }

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
     * 获取首页轮播图列表（前台接口：按 sort 倒序取前 5 张）
     */
    @GetMapping("/banner")
    public Result<List<BannerItemDTO>> getBanner() {
        return Result.success(configService.getBannerList());
    }

    /**
     * 后台：获取轮播图完整列表（用于上传与拖动排序）
     */
    @GetMapping("/admin/banner")
    public Result<List<BannerItemDTO>> getBannerForAdmin() {
        return Result.success(configService.getBannerListForAdmin());
    }

    /**
     * 后台：上传图片，返回相对路径 URL。
     * type=banner 存 uploads/banner/，type=scholar 存 uploads/scholar/，默认 banner。
     */
    @PostMapping("/admin/upload")
    public Result<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "banner") String type) {
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择图片文件");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            return Result.fail("无法识别文件类型");
        }
        String subDir = "scholar".equalsIgnoreCase(type) ? "scholar" : "news".equalsIgnoreCase(type) ? "news" : "banner";
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            Path base = Paths.get(uploadDir).isAbsolute()
                    ? Paths.get(uploadDir)
                    : Paths.get(System.getProperty("user.dir"), uploadDir);
            base = base.toAbsolutePath().normalize();
            Path dir = base.resolve(subDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            String url = "/uploads/" + subDir + "/" + filename;
            return Result.success(url);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.fail("上传失败，请稍后重试");
        }
    }

    /**
     * 更新首页轮播图列表（后台管理接口）
     */
    @PutMapping("/admin/banner")
    public Result<Void> updateBanner(@RequestBody List<BannerItemDTO> list) {
        configService.updateBannerList(list);
        return Result.success();
    }

    /**
     * 后台：获取网站标题
     */
    @GetMapping("/admin/site-title")
    public Result<String> getSiteTitleForAdmin() {
        return Result.success(configService.getSiteTitle());
    }

    /**
     * 后台：更新网站标题（body 传 {"title":"xxx"} 或 纯字符串）
     */
    @PutMapping("/admin/site-title")
    public Result<Void> updateSiteTitle(@RequestBody java.util.Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        if (title == null && body != null && body.containsKey("title")) {
            title = "";
        }
        configService.updateSiteTitle(title != null ? title : "实验室网站");
        return Result.success();
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
