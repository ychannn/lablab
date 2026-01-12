package org.ychan.lablab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.AchievementAddReqDTO;
import org.ychan.lablab.dto.req.AchievementUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.AchievementRespDTO;
import org.ychan.lablab.entity.science.Achievement;

import java.util.List;

public interface AchievementService extends IService<Achievement> {

    /**
     * 获取所有成果奖项列表
     * @return
     */
    List<AchievementRespDTO> listAchievement();

    /**
     * 根据id获取成果奖项详情
     * @param id
     * @return
     */
    AchievementRespDTO getAchievementById(Integer id);

    /**
     * 新增成果奖项
     * @param requestParam
     */
    void addAchievement(AchievementAddReqDTO requestParam);

    /**
     * 修改成果奖项
     * @param requestParam
     */
    void updateAchievement(AchievementUpdateReqDTO requestParam);

    /**
     * 删除成果奖项
     * @param id
     */
    void deleteAchievementById(Integer id);
}

