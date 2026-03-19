package org.ychan.lablab.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.LabNewsAddReqDTO;
import org.ychan.lablab.dto.req.LabNewsUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.LabNewsRespDTO;
import org.ychan.lablab.entity.news.LabNews;

import java.util.List;

public interface LabNewsService extends IService<LabNews> {

    /**
     * 获取所有实验室新闻列表
     * @return
     */
    List<LabNewsRespDTO> listLabNews();

    /**
     * 分页获取实验室新闻（支持关键词、时间范围筛选）
     */
    IPage<LabNewsRespDTO> pageLabNews(int pageNum, int pageSize, String keyword, String timeStart, String timeEnd);

    /**
     * 根据id获取实验室新闻详情
     * @param id
     * @return
     */
    LabNewsRespDTO getLabNewsById(Integer id);

    /**
     * 新增实验室新闻
     * @param requestParam
     */
    void addLabNews(LabNewsAddReqDTO requestParam);

    /**
     * 修改实验室新闻
     * @param requestParam
     */
    void updateLabNews(LabNewsUpdateReqDTO requestParam);

    /**
     * 删除实验室新闻
     * @param id
     */
    void deleteLabNewsById(Integer id);
}


