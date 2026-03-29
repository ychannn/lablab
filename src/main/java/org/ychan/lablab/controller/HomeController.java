package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ychan.lablab.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ychan.lablab.dto.resp.config.BannerItemDTO;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;
import org.ychan.lablab.dto.resp.news.LabNewsRespDTO;
import org.ychan.lablab.dto.resp.news.NoticeRespDTO;
import org.ychan.lablab.dto.resp.research.*;
import org.ychan.lablab.service.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final LabNewsService labNewsService;
    private final PaperPublicationService paperPublicationService;
    private final TopicProjectService topicProjectService;
    private final AchievementService achievementService;
    private final ConfigService configService;
    private final NoticeService noticeService;

    /**
     * 首页聚合接口
     */
    @GetMapping
    public Result<HomeRespDTO> getHomeData() {
        HomeRespDTO resp = new HomeRespDTO();

        // 网站标题（可后台修改）
        resp.setSiteTitle(configService.getSiteTitle());
        // 轮播图：从 config 读取，后台配置、门户只展示图片
        resp.setBannerList(configService.getBannerList());

        // 获取最新成果（最新6条，混合类型）
        List<HomeAchievementDTO> achievementList = new ArrayList<>();

        // 获取最新论文
        List<PaperPublicationRespDTO> paperList = paperPublicationService.listPaperPublication();
        for (PaperPublicationRespDTO paper : paperList) {
            if (achievementList.size() >= 6) break;
            HomeAchievementDTO dto = new HomeAchievementDTO();
            dto.setId(paper.getId());
            dto.setType("paper");
            dto.setTitle(paper.getContent());
            dto.setTime(paper.getPublishTime());
            achievementList.add(dto);
        }

        // 获取最新项目
        List<TopicProjectRespDTO> projectList = topicProjectService.listTopicProject();
        for (TopicProjectRespDTO project : projectList) {
            if (achievementList.size() >= 6) break;
            HomeAchievementDTO dto = new HomeAchievementDTO();
            dto.setId(project.getId());
            dto.setType("project");
            dto.setTitle(project.getContent());
            dto.setTime(project.getStartTime());
            achievementList.add(dto);
        }

        // 获取最新奖项
        List<AchievementRespDTO> awardList = achievementService.listAchievement();
        for (AchievementRespDTO award : awardList) {
            if (achievementList.size() >= 6) break;
            HomeAchievementDTO dto = new HomeAchievementDTO();
            dto.setId(award.getId());
            dto.setType("award");
            dto.setTitle(award.getContent());
            dto.setTime(award.getCreateTime());
            achievementList.add(dto);
        }

        resp.setLatestAchievements(achievementList);

        // 首页左侧：最新 5 条新闻（轮播，含标题、时间、内容开头）
        IPage<LabNewsRespDTO> newsPage = labNewsService.pageLabNews(1, 5, null, null, null);
        List<HomeNewsItemDTO> latestNews = new ArrayList<>();
        for (LabNewsRespDTO n : newsPage.getRecords()) {
            HomeNewsItemDTO dto = new HomeNewsItemDTO();
            dto.setId(n.getId());
            dto.setTitle(n.getTitle());
            dto.setTime(n.getTime());
            dto.setImageUrl(n.getImageUrl());
            String content = n.getContent() != null ? n.getContent() : "";
            dto.setContentSnippet(content.length() > 80 ? content.substring(0, 80) + "…" : content);
            latestNews.add(dto);
        }
        resp.setLatestNews(latestNews);

        // 最新 5 条公告（首页右侧）
        IPage<NoticeRespDTO> noticePage = noticeService.pageNotice(1, 5, null, null, null);
        resp.setLatestNotices(noticePage.getRecords());

        // 获取实验室简介
        LabIntroRespDTO labIntro = configService.getLabIntro();
        resp.setLabIntro(labIntro);

        // 获取联系方式
        ContactRespDTO contact = configService.getContactInfo();
        resp.setContact(contact);

        return Result.success(resp);
    }

    /**
     * 首页响应DTO
     */
    public static class HomeRespDTO {
        /** 网站标题（导航、页脚、浏览器标题） */
        private String siteTitle;
        /** 首页轮播图列表（config 配置，仅展示图片） */
        private List<BannerItemDTO> bannerList;
        private List<HomeAchievementDTO> latestAchievements;
        /** 最新 5 条新闻（首页左侧轮播） */
        private List<HomeNewsItemDTO> latestNews;
        /** 最新 5 条公告（首页右侧） */
        private List<NoticeRespDTO> latestNotices;
        private LabIntroRespDTO labIntro;
        private ContactRespDTO contact;

        public String getSiteTitle() {
            return siteTitle;
        }

        public void setSiteTitle(String siteTitle) {
            this.siteTitle = siteTitle;
        }

        public List<BannerItemDTO> getBannerList() {
            return bannerList;
        }

        public void setBannerList(List<BannerItemDTO> bannerList) {
            this.bannerList = bannerList;
        }

        public List<HomeAchievementDTO> getLatestAchievements() {
            return latestAchievements;
        }

        public void setLatestAchievements(List<HomeAchievementDTO> latestAchievements) {
            this.latestAchievements = latestAchievements;
        }

        public List<HomeNewsItemDTO> getLatestNews() {
            return latestNews;
        }

        public void setLatestNews(List<HomeNewsItemDTO> latestNews) {
            this.latestNews = latestNews;
        }

        public List<NoticeRespDTO> getLatestNotices() {
            return latestNotices;
        }

        public void setLatestNotices(List<NoticeRespDTO> latestNotices) {
            this.latestNotices = latestNotices;
        }

        public LabIntroRespDTO getLabIntro() {
            return labIntro;
        }

        public void setLabIntro(LabIntroRespDTO labIntro) {
            this.labIntro = labIntro;
        }

        public ContactRespDTO getContact() {
            return contact;
        }

        public void setContact(ContactRespDTO contact) {
            this.contact = contact;
        }
    }

    /**
     * 首页成果DTO
     */
    public static class HomeAchievementDTO {
        private Integer id;
        private String type; // paper, project, award
        private String title;
        private java.time.LocalDateTime time;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public java.time.LocalDateTime getTime() {
            return time;
        }

        public void setTime(java.time.LocalDateTime time) {
            this.time = time;
        }
    }

    /**
     * 首页新闻项（左侧轮播）
     */
    public static class HomeNewsItemDTO {
        private Integer id;
        private String title;
        private java.time.LocalDateTime time;
        private String imageUrl;
        private String contentSnippet;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public java.time.LocalDateTime getTime() { return time; }
        public void setTime(java.time.LocalDateTime time) { this.time = time; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getContentSnippet() { return contentSnippet; }
        public void setContentSnippet(String contentSnippet) { this.contentSnippet = contentSnippet; }
    }
}