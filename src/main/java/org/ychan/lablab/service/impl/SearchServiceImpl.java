package org.ychan.lablab.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ychan.lablab.document.SearchDocument;
import org.ychan.lablab.dto.resp.search.SearchRespDTO;
import org.ychan.lablab.entity.news.LabNews;
import org.ychan.lablab.entity.news.Notice;
import org.ychan.lablab.entity.science.Achievement;
import org.ychan.lablab.entity.science.PaperPublication;
import org.ychan.lablab.entity.science.TopicProject;
import org.ychan.lablab.entity.team.Scholar;
import org.ychan.lablab.mapper.*;
import org.ychan.lablab.service.SearchService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现 - 使用数据库搜索
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final LabNewsMapper labNewsMapper;
    private final NoticeMapper noticeMapper;
    private final ScholarMapper scholarMapper;
    private final AchievementMapper achievementMapper;
    private final PaperPublicationMapper paperPublicationMapper;
    private final TopicProjectMapper topicProjectMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public SearchRespDTO search(String keyword, int page, int size) {
        List<SearchRespDTO.SearchItemDTO> allResults = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            SearchRespDTO empty = new SearchRespDTO();
            empty.setTotal(0);
            empty.setPage(page);
            empty.setSize(size);
            empty.setTotalPages(0);
            empty.setItems(allResults);
            return empty;
        }
        // 从各个表中搜索
        allResults.addAll(searchNews(keyword));
        allResults.addAll(searchNotices(keyword));
        allResults.addAll(searchScholars(keyword));
        allResults.addAll(searchAchievements(keyword));
        allResults.addAll(searchPapers(keyword));
        allResults.addAll(searchProjects(keyword));
        
        // 分页处理
        int total = allResults.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<SearchRespDTO.SearchItemDTO> pageResults = start < total ? 
            allResults.subList(start, end) : new ArrayList<>();
        
        SearchRespDTO response = new SearchRespDTO();
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages(totalPages);
        response.setItems(pageResults);
        
        return response;
    }

    private List<SearchRespDTO.SearchItemDTO> searchNews(String keyword) {
        List<LabNews> newsList = labNewsMapper.selectList(null);
        return newsList.stream()
            .filter(news -> containsKeyword(news.getTitle(), keyword) || 
                           containsKeyword(news.getContent(), keyword))
            .map(news -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("news_" + news.getId());
                item.setType("news");
                item.setTitle(news.getTitle());
                item.setContent(truncateContent(news.getContent(), 200));
                item.setCreateTime(news.getTime() != null ? news.getTime().format(FORMATTER) : "");
                item.setUrl("/news/detail/" + news.getId());
                return item;
            })
            .collect(Collectors.toList());
    }

    private List<SearchRespDTO.SearchItemDTO> searchNotices(String keyword) {
        List<Notice> noticeList = noticeMapper.selectList(null);
        return noticeList.stream()
            .filter(notice -> containsKeyword(notice.getTitle(), keyword) || 
                              containsKeyword(notice.getContent(), keyword))
            .map(notice -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("notice_" + notice.getId());
                item.setType("notice");
                item.setTitle(notice.getTitle());
                item.setContent(truncateContent(notice.getContent(), 200));
                item.setCreateTime(notice.getTime() != null ? notice.getTime().format(FORMATTER) : "");
                item.setUrl("/notice/detail/" + notice.getId());
                return item;
            })
            .collect(Collectors.toList());
    }

    private List<SearchRespDTO.SearchItemDTO> searchScholars(String keyword) {
        List<Scholar> scholarList = scholarMapper.selectList(null);
        return scholarList.stream()
            .filter(scholar -> containsKeyword(scholar.getName(), keyword) || 
                               containsKeyword(scholar.getEmail(), keyword))
            .map(scholar -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("scholar_" + scholar.getId());
                item.setType("scholar");
                item.setTitle(scholar.getName());
                item.setContent(scholar.getEmail());
                item.setCreateTime(scholar.getCreateTime() != null ? scholar.getCreateTime().format(FORMATTER) : "");
                item.setUrl("/scholar/detail/" + scholar.getId());
                item.setImageUrl(scholar.getPhoto());
                return item;
            })
            .collect(Collectors.toList());
    }

    private List<SearchRespDTO.SearchItemDTO> searchAchievements(String keyword) {
        List<Achievement> achievementList = achievementMapper.selectList(null);
        return achievementList.stream()
            .filter(achievement -> containsKeyword(achievement.getContent(), keyword))
            .map(achievement -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("achievement_" + achievement.getId());
                item.setType("achievement");
                item.setContent(truncateContent(achievement.getContent(), 200));
                item.setCreateTime(achievement.getCreateTime() != null ? achievement.getCreateTime().format(FORMATTER) : "");
                item.setUrl("/achievement/detail/" + achievement.getId());
                return item;
            })
            .collect(Collectors.toList());
    }

    private List<SearchRespDTO.SearchItemDTO> searchPapers(String keyword) {
        List<PaperPublication> paperList = paperPublicationMapper.selectList(null);
        return paperList.stream()
            .filter(paper -> containsKeyword(paper.getContent(), keyword))
            .map(paper -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("paper_" + paper.getId());
                item.setType("paper");
                item.setContent(truncateContent(paper.getContent(), 200));
                item.setCreateTime(paper.getPublishTime() != null ? paper.getPublishTime().format(FORMATTER) : "");
                item.setUrl("/paper/detail/" + paper.getId());
                return item;
            })
            .collect(Collectors.toList());
    }

    private List<SearchRespDTO.SearchItemDTO> searchProjects(String keyword) {
        List<TopicProject> projectList = topicProjectMapper.selectList(null);
        return projectList.stream()
            .filter(project -> containsKeyword(project.getContent(), keyword))
            .map(project -> {
                SearchRespDTO.SearchItemDTO item = new SearchRespDTO.SearchItemDTO();
                item.setId("project_" + project.getId());
                item.setType("project");
                item.setContent(truncateContent(project.getContent(), 200));
                item.setCreateTime(project.getStartTime() != null ? project.getStartTime().format(FORMATTER) : "");
                item.setUrl("/project/detail/" + project.getId());
                return item;
            })
            .collect(Collectors.toList());
    }

    private boolean containsKeyword(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }

    @Override
    public void syncData() {
        // 数据库搜索不需要同步数据
    }

    @Override
    public void addDocument(SearchDocument document) {
        // 数据库搜索不需要添加文档
    }

    @Override
    public void deleteDocument(String id) {
        // 数据库搜索不需要删除文档
    }

    @Override
    public void addDocuments(List<SearchDocument> documents) {
        // 数据库搜索不需要批量添加文档
    }
}
