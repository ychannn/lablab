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
import org.ychan.lablab.entity.config.FriendLink;
import org.ychan.lablab.mapper.FriendLinkMapper;
import org.ychan.lablab.service.ConfigService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final FriendLinkMapper friendLinkMapper;

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

    /**
     * 获取友情链接列表（前台接口）
     */
    @GetMapping("/friend-links")
    public Result<List<FriendLink>> getFriendLinks() {
        return Result.success(configService.getFriendLinks());
    }

    /**
     * 添加友情链接（后台管理接口）
     */
    @PostMapping("/friend-links")
    public Result<Void> addFriendLink(@RequestBody FriendLink link) {
        if (link.getName() == null || link.getName().isBlank()) {
            return Result.fail("链接名称不能为空");
        }
        if (link.getUrl() == null || link.getUrl().isBlank()) {
            return Result.fail("链接地址不能为空");
        }
        link.setSort(0);
        link.setEnabled(true);
        friendLinkMapper.insert(link);
        return Result.success();
    }

    /**
     * 更新友情链接（后台管理接口）
     */
    @PutMapping("/friend-links")
    public Result<Void> updateFriendLink(@RequestBody FriendLink link) {
        if (link.getId() == null) {
            return Result.fail("链接ID不能为空");
        }
        friendLinkMapper.updateById(link);
        return Result.success();
    }

    /**
     * 删除友情链接（后台管理接口）
     */
    @DeleteMapping("/friend-links/{id}")
    public Result<Void> deleteFriendLink(@PathVariable Integer id) {
        friendLinkMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 后台：批量获取所有配置（网站标题、页面标题、联系信息）
     */
    @GetMapping("/admin/all")
    public Result<Map<String, Object>> getAllConfigs() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("siteTitle", configService.getSiteTitle());
        result.put("homeCarouselTitle", configService.getConfigValue("homeCarouselTitle", "新闻动态"));
        result.put("campusTitle", configService.getConfigValue("campusTitle", "校园风景"));
        result.put("labNewsTitle", configService.getConfigValue("labNewsTitle", "实验室动态"));
        result.put("noticeTitle", configService.getConfigValue("noticeTitle", "公告通知"));
        result.put("friendLinkTitle", configService.getConfigValue("friendLinkTitle", "友情链接"));
        result.put("labEnvTitle", configService.getConfigValue("labEnvTitle", "实验室环境"));
        
        ContactRespDTO contact = configService.getContactInfo();
        result.put("contact", contact);
        
        return Result.success(result);
    }

    /**
     * 后台：批量更新配置
     */
    @PutMapping("/admin/all")
    public Result<Void> updateAllConfigs(@RequestBody Map<String, Object> config) {
        if (config.containsKey("siteTitle")) {
            configService.updateSiteTitle(String.valueOf(config.get("siteTitle")));
        }
        
        String[] titleKeys = {"homeCarouselTitle", "campusTitle", "labNewsTitle", "noticeTitle", "friendLinkTitle", "labEnvTitle"};
        for (String key : titleKeys) {
            if (config.containsKey(key)) {
                configService.updateConfigValue(key, String.valueOf(config.get(key)));
            }
        }
        
        if (config.containsKey("contact")) {
            Map<String, Object> contactMap = (Map<String, Object>) config.get("contact");
            ContactRespDTO contactDTO = new ContactRespDTO();
            if (contactMap.containsKey("address")) contactDTO.setAddress(String.valueOf(contactMap.get("address")));
            if (contactMap.containsKey("phone")) contactDTO.setPhone(String.valueOf(contactMap.get("phone")));
            if (contactMap.containsKey("email")) contactDTO.setEmail(String.valueOf(contactMap.get("email")));
            configService.updateContactInfo(contactDTO);
        }
        
        return Result.success();
    }
}
