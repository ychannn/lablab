package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.dto.resp.config.ContactRespDTO;
import org.ychan.lablab.dto.resp.config.LabIntroRespDTO;
import org.ychan.lablab.dto.resp.news.LabNewsRespDTO;
import org.ychan.lablab.dto.resp.research.*;
import org.ychan.lablab.service.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final LabNewsService labNewsService;
    private final PaperPublicationService paperPublicationService;
    private final TopicProjectService topicProjectService;
    private final AchievementService achievementService;
    private final ConfigService configService;

    /**
     * 首页聚合接口
     */
    @GetMapping
    public Result<HomeRespDTO> getHomeData() {
        HomeRespDTO resp = new HomeRespDTO();

        // 获取轮播图新闻（最新3条）
        List<LabNewsRespDTO> newsList = labNewsService.listLabNews();
        if (newsList.size() > 3) {
            newsList = newsList.subList(0, 3);
        }
        resp.setBannerNews(newsList);

        // 获取最新成果（最新6条，混合类型）
        List<HomeAchievementDTO> achievementList = new ArrayList<>();

        // 获取最新论文
        List<PaperPublicationRespDTO> paperList = paperPublicationService.listPaperPublication();
        for (PaperPublicationRespDTO paper : paperList) {
            if (achievementList.size() >= 6) break;
            HomeAchievementDTO dto = new HomeAchievementDTO();
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
            dto.setType("award");
            dto.setTitle(award.getContent());
            dto.setTime(award.getCreateTime());
            achievementList.add(dto);
        }

        resp.setLatestAchievements(achievementList);

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
        private List<LabNewsRespDTO> bannerNews;
        private List<HomeAchievementDTO> latestAchievements;
        private LabIntroRespDTO labIntro;
        private ContactRespDTO contact;

        public List<LabNewsRespDTO> getBannerNews() {
            return bannerNews;
        }

        public void setBannerNews(List<LabNewsRespDTO> bannerNews) {
            this.bannerNews = bannerNews;
        }

        public List<HomeAchievementDTO> getLatestAchievements() {
            return latestAchievements;
        }

        public void setLatestAchievements(List<HomeAchievementDTO> latestAchievements) {
            this.latestAchievements = latestAchievements;
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
        private String type; // paper, project, award
        private String title;
        private java.time.LocalDateTime time;

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
}