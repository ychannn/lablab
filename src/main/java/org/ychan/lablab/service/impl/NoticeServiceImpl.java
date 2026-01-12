package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.NoticeAddReqDTO;
import org.ychan.lablab.dto.req.NoticeUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.NoticeRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.news.Notice;
import org.ychan.lablab.mapper.NoticeMapper;
import org.ychan.lablab.service.NoticeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    /**
     * 获取所有通知列表
     * @return
     */
    @Override
    public List<NoticeRespDTO> listNotice() {
        List<Notice> noticeList = lambdaQuery()
                .eq(Notice::getDeleted, CommonConstants.FALSE)
                .orderByDesc(Notice::getCreateTime)
                .list();
        return noticeList.stream()
                .map(notice -> {
                    NoticeRespDTO respDTO = new NoticeRespDTO();
                    BeanUtils.copyProperties(notice, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取通知详情
     * @param id
     * @return
     */
    @Override
    public NoticeRespDTO getNoticeById(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || notice.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("通知不存在");
        }
        NoticeRespDTO respDTO = new NoticeRespDTO();
        BeanUtils.copyProperties(notice, respDTO);
        return respDTO;
    }

    /**
     * 新增通知
     * @param requestParam
     */
    @Override
    @Transactional
    public void addNotice(NoticeAddReqDTO requestParam) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(requestParam, notice);
        int insert = noticeMapper.insert(notice);
        if (insert < 1) {
            throw new BusinessException("新增通知失败");
        }
    }

    /**
     * 修改通知
     * @param requestParam
     */
    @Override
    @Transactional
    public void updateNotice(NoticeUpdateReqDTO requestParam) {
        Notice notice = noticeMapper.selectById(requestParam.getId());
        if (notice == null || notice.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("通知不存在");
        }

        BeanUtils.copyProperties(requestParam, notice);
        int update = noticeMapper.updateById(notice);
        if (update < 1) {
            throw new BusinessException("修改通知失败");
        }
    }

    /**
     * 删除通知
     * @param id
     */
    @Override
    @Transactional
    public void deleteNoticeById(Integer id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || notice.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("通知不存在");
        }
        notice.setDeleted(CommonConstants.TRUE);
        noticeMapper.updateById(notice);
    }
}


