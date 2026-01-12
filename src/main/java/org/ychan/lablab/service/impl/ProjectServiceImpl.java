package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.ychan.lablab.entity.team.Project;
import org.ychan.lablab.mapper.ProjectMapper;
import org.ychan.lablab.service.ProjectService;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
}
