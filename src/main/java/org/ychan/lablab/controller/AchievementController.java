package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.AchievementAddReqDTO;
import org.ychan.lablab.dto.req.AchievementUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.AchievementRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.AchievementService;

import java.util.List;

@RestController
@RequestMapping("/achievement")
@RequiredArgsConstructor
public class AchievementController extends BaseController {

    private final AchievementService achievementService;

    /**
     * 获取所有成果奖项列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<AchievementRespDTO>> listAchievement() {
        return Result.success(achievementService.listAchievement());
    }

    /**
     * 分页获取所有成果奖项列表
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<AchievementRespDTO>> pageAchievement(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<AchievementRespDTO> list = achievementService.pageAchievement(pageNum, pageSize, keyword);
        return Result.success(list);
    }

    /**
     * 根据id获取成果奖项详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AchievementRespDTO> getAchievementById(@PathVariable Integer id) {
        return Result.success(achievementService.getAchievementById(id));
    }

    /**
     * 新增成果奖项
     * @param requestParam
     * @return
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addAchievement(@RequestBody AchievementAddReqDTO requestParam) {
        achievementService.addAchievement(requestParam);
        return Result.success();
    }

    /**
     * 修改成果奖项
     * @param requestParam
     * @return
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> updateAchievement(@RequestBody AchievementUpdateReqDTO requestParam) {
        achievementService.updateAchievement(requestParam);
        return Result.success();
    }

    /**
     * 删除成果奖项
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> deleteAchievementById(@PathVariable Integer id) {
        achievementService.deleteAchievementById(id);
        return Result.success();
    }
}

