package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.TopicProjectAddReqDTO;
import org.ychan.lablab.dto.req.TopicProjectUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.TopicProjectRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.science.TopicProject;
import org.ychan.lablab.mapper.TopicProjectMapper;
import org.ychan.lablab.service.TopicProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicProjectServiceImpl extends ServiceImpl<TopicProjectMapper, TopicProject> implements TopicProjectService {

    private final TopicProjectMapper topicProjectMapper;

    /**
     * 获取所有课题项目列表
     * @return
     */
    @Override
    public List<TopicProjectRespDTO> listTopicProject() {
        List<TopicProject> projectList = lambdaQuery()
                .eq(TopicProject::getDeleted, CommonConstants.FALSE)
                .orderByDesc(TopicProject::getCreateTime)
                .list();
        return projectList.stream()
                .map(project -> {
                    TopicProjectRespDTO respDTO = new TopicProjectRespDTO();
                    BeanUtils.copyProperties(project, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取课题项目详情
     * @param id
     * @return
     */
    @Override
    public TopicProjectRespDTO getTopicProjectById(Integer id) {
        TopicProject project = topicProjectMapper.selectById(id);
        if (project == null || project.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("课题项目不存在");
        }
        TopicProjectRespDTO respDTO = new TopicProjectRespDTO();
        BeanUtils.copyProperties(project, respDTO);
        return respDTO;
    }

    /**
     * 新增课题项目
     * @param requestParam
     */
    @Override
    @Transactional
    public void addTopicProject(TopicProjectAddReqDTO requestParam) {
        TopicProject project = new TopicProject();
        BeanUtils.copyProperties(requestParam, project);
        int insert = topicProjectMapper.insert(project);
        if (insert < 1) {
            throw new BusinessException("新增课题项目失败");
        }
    }

    /**
     * 修改课题项目
     * @param requestParam
     */
    @Override
    @Transactional
    public void updateTopicProject(TopicProjectUpdateReqDTO requestParam) {
        TopicProject project = topicProjectMapper.selectById(requestParam.getId());
        if (project == null || project.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("课题项目不存在");
        }

        BeanUtils.copyProperties(requestParam, project);
        int update = topicProjectMapper.updateById(project);
        if (update < 1) {
            throw new BusinessException("修改课题项目失败");
        }
    }

    /**
     * 删除课题项目
     * @param id
     */
    @Override
    @Transactional
    public void deleteTopicProjectById(Integer id) {
        TopicProject project = topicProjectMapper.selectById(id);
        if (project == null || project.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("课题项目不存在");
        }
        project.setDeleted(CommonConstants.TRUE);
        topicProjectMapper.updateById(project);
    }
}

