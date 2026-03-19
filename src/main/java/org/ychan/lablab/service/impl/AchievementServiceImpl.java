package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.AchievementAddReqDTO;
import org.ychan.lablab.dto.req.AchievementUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.AchievementRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.science.Achievement;
import org.ychan.lablab.mapper.AchievementMapper;
import org.ychan.lablab.service.AchievementService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl extends ServiceImpl<AchievementMapper, Achievement> implements AchievementService {

    private final AchievementMapper achievementMapper;

    /**
     * 获取所有成果奖项列表
     * @return
     */
    @Override
    public List<AchievementRespDTO> listAchievement() {
        List<Achievement> achievementList = lambdaQuery()
                .eq(Achievement::getDeleted, CommonConstants.FALSE)
                .orderByDesc(Achievement::getCreateTime)
                .list();
        return achievementList.stream()
                .map(achievement -> {
                    AchievementRespDTO respDTO = new AchievementRespDTO();
                    BeanUtils.copyProperties(achievement, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public IPage<AchievementRespDTO> pageAchievement(int pageNum, int pageSize, String keyword) {
        var wrapper = lambdaQuery()
                .eq(Achievement::getDeleted, CommonConstants.FALSE);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Achievement::getContent, keyword);
        }
        IPage<Achievement> page = wrapper.orderByDesc(Achievement::getCreateTime).page(new Page<>(pageNum, pageSize));
        return page.convert(AchievementRespDTO::from);
    }

    /**
     * 根据id获取成果奖项详情
     * @param id
     * @return
     */
    @Override
    public AchievementRespDTO getAchievementById(Integer id) {
        Achievement achievement = achievementMapper.selectById(id);
        if (achievement == null || achievement.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("成果奖项不存在");
        }
        AchievementRespDTO respDTO = new AchievementRespDTO();
        BeanUtils.copyProperties(achievement, respDTO);
        return respDTO;
    }

    /**
     * 新增成果奖项
     * @param requestParam
     */
    @Override
    @Transactional
    public void addAchievement(AchievementAddReqDTO requestParam) {
        Achievement achievement = new Achievement();
        BeanUtils.copyProperties(requestParam, achievement);
        int insert = achievementMapper.insert(achievement);
        if (insert < 1) {
            throw new BusinessException("新增成果奖项失败");
        }
    }

    /**
     * 修改成果奖项
     * @param requestParam
     */
    @Override
    @Transactional
    public void updateAchievement(AchievementUpdateReqDTO requestParam) {
        Achievement achievement = achievementMapper.selectById(requestParam.getId());
        if (achievement == null || achievement.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("成果奖项不存在");
        }

        BeanUtils.copyProperties(requestParam, achievement);
        int update = achievementMapper.updateById(achievement);
        if (update < 1) {
            throw new BusinessException("修改成果奖项失败");
        }
    }

    /**
     * 删除成果奖项
     * @param id
     */
    @Override
    @Transactional
    public void deleteAchievementById(Integer id) {
        Achievement achievement = achievementMapper.selectById(id);
        if (achievement == null || achievement.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("成果奖项不存在");
        }
        achievement.setDeleted(CommonConstants.TRUE);
        achievementMapper.updateById(achievement);
    }
}

