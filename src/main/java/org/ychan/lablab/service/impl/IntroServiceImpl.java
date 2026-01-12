package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.ychan.lablab.entity.team.Intro;
import org.ychan.lablab.mapper.IntroMapper;
import org.ychan.lablab.service.IntroService;

@Service
public class IntroServiceImpl extends ServiceImpl<IntroMapper, Intro> implements IntroService {
}
