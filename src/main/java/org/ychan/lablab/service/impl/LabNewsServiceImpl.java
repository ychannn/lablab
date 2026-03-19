package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.LabNewsAddReqDTO;
import org.ychan.lablab.dto.req.LabNewsUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.LabNewsRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.news.LabNews;
import org.ychan.lablab.mapper.LabNewsMapper;
import org.ychan.lablab.service.LabNewsService;
import org.ychan.lablab.util.DateTimeParseUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabNewsServiceImpl extends ServiceImpl<LabNewsMapper, LabNews> implements LabNewsService {

    private final LabNewsMapper labNewsMapper;

    /**
     * 获取所有实验室新闻列表
     * @return
     */
    @Override
    public List<LabNewsRespDTO> listLabNews() {
        List<LabNews> newsList = lambdaQuery()
                .eq(LabNews::getDeleted, CommonConstants.FALSE)
                .orderByDesc(LabNews::getCreateTime)
                .list();
        return newsList.stream().map(LabNewsRespDTO::from).collect(Collectors.toList());
    }

    @Override
    public IPage<LabNewsRespDTO> pageLabNews(int pageNum, int pageSize, String keyword, String timeStart, String timeEnd) {
        var wrapper = lambdaQuery()
                .eq(LabNews::getDeleted, CommonConstants.FALSE);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(LabNews::getTitle, keyword).or().like(LabNews::getContent, keyword));
        }
        var start = DateTimeParseUtil.parse(timeStart);
        if (start != null) wrapper.ge(LabNews::getTime, start);
        var end = DateTimeParseUtil.parse(timeEnd);
        if (end != null) wrapper.le(LabNews::getTime, end);
        IPage<LabNews> page = wrapper.orderByDesc(LabNews::getCreateTime).page(new Page<>(pageNum, pageSize));
        return page.convert(LabNewsRespDTO::from);
    }

    /**
     * 根据id获取实验室新闻详情
     * @param id
     * @return
     */
    @Override
    public LabNewsRespDTO getLabNewsById(Integer id) {
        LabNews news = labNewsMapper.selectById(id);
        if (news == null || news.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("实验室新闻不存在");
        }
        return LabNewsRespDTO.from(news);
    }

    /**
     * 新增实验室新闻
     * @param requestParam
     */
    @Override
    @Transactional
    public void addLabNews(LabNewsAddReqDTO requestParam) {
        LabNews news = new LabNews();
        BeanUtils.copyProperties(requestParam, news);
        news.setImages(requestParam.getImageUrl() != null && !requestParam.getImageUrl().isBlank() ? requestParam.getImageUrl().trim() : null);
        int insert = labNewsMapper.insert(news);
        if (insert < 1) {
            throw new BusinessException("新增实验室新闻失败");
        }
    }

    /**
     * 修改实验室新闻
     * @param requestParam
     */
    @Override
    @Transactional
    public void updateLabNews(LabNewsUpdateReqDTO requestParam) {
        LabNews news = labNewsMapper.selectById(requestParam.getId());
        if (news == null || news.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("实验室新闻不存在");
        }

        BeanUtils.copyProperties(requestParam, news);
        news.setImages(requestParam.getImageUrl() != null && !requestParam.getImageUrl().isBlank() ? requestParam.getImageUrl().trim() : null);
        int update = labNewsMapper.updateById(news);
        if (update < 1) {
            throw new BusinessException("修改实验室新闻失败");
        }
    }

    /**
     * 删除实验室新闻
     * @param id
     */
    @Override
    @Transactional
    public void deleteLabNewsById(Integer id) {
        LabNews news = labNewsMapper.selectById(id);
        if (news == null || news.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("实验室新闻不存在");
        }
        news.setDeleted(CommonConstants.TRUE);
        labNewsMapper.updateById(news);
    }
}


