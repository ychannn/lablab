package org.ychan.lablab.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.TopicProjectAddReqDTO;
import org.ychan.lablab.dto.req.TopicProjectUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.TopicProjectRespDTO;
import org.ychan.lablab.entity.science.TopicProject;

import java.util.List;

public interface TopicProjectService extends IService<TopicProject> {

    /**
     * 获取所有课题项目列表
     * @return
     */
    List<TopicProjectRespDTO> listTopicProject();

    /**
     * 分页获取课题项目（支持关键词、开始/结束时间范围筛选）
     */
    IPage<TopicProjectRespDTO> pageTopicProject(int pageNum, int pageSize, String keyword, String startTimeStart, String startTimeEnd, String endTimeStart, String endTimeEnd);

    /**
     * 根据id获取课题项目详情
     * @param id
     * @return
     */
    TopicProjectRespDTO getTopicProjectById(Integer id);

    /**
     * 新增课题项目
     * @param requestParam
     */
    void addTopicProject(TopicProjectAddReqDTO requestParam);

    /**
     * 修改课题项目
     * @param requestParam
     */
    void updateTopicProject(TopicProjectUpdateReqDTO requestParam);

    /**
     * 删除课题项目
     * @param id
     */
    void deleteTopicProjectById(Integer id);
}

