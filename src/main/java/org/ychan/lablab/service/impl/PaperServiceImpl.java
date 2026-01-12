package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.ychan.lablab.entity.team.Paper;
import org.ychan.lablab.mapper.PaperMapper;
import org.ychan.lablab.service.PaperService;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {
}
