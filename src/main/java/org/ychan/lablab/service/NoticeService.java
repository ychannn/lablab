package org.ychan.lablab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.NoticeAddReqDTO;
import org.ychan.lablab.dto.req.NoticeUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.NoticeRespDTO;
import org.ychan.lablab.entity.news.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {

    /**
     * 获取所有通知列表
     * @return
     */
    List<NoticeRespDTO> listNotice();

    /**
     * 根据id获取通知详情
     * @param id
     * @return
     */
    NoticeRespDTO getNoticeById(Integer id);

    /**
     * 新增通知
     * @param requestParam
     */
    void addNotice(NoticeAddReqDTO requestParam);

    /**
     * 修改通知
     * @param requestParam
     */
    void updateNotice(NoticeUpdateReqDTO requestParam);

    /**
     * 删除通知
     * @param id
     */
    void deleteNoticeById(Integer id);
}


