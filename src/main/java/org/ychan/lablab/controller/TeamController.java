package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.config.lock.EntityLock;
import org.ychan.lablab.dto.req.TeamAddScholarReqDTO;
import org.ychan.lablab.dto.req.TeamUpdateScholarReqDTO;
import org.ychan.lablab.dto.resp.team.TeamScholarDetailsRespDTO;
import org.ychan.lablab.dto.resp.team.TeamBasicScholarRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.dto.resp.team.ScholarRankOptionDTO;
import org.ychan.lablab.enums.ScholarRankEnum;
import org.ychan.lablab.service.ScholarService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final ScholarService scholarService;
    /**
     * 返回学者基础信息列表（门户用）
     */
    @GetMapping("/scholar/basic-list")
    public List<TeamBasicScholarRespDTO> listBasicScholar(){
        return scholarService.listBasicScholar();
    }

    /**
     * 职级列表（后台下拉用）：教授、副教授、讲师、助教，按职级高到低
     */
    @GetMapping("/scholar/ranks")
    public Result<List<ScholarRankOptionDTO>> listScholarRanks() {
        List<ScholarRankOptionDTO> list = Arrays.stream(ScholarRankEnum.values())
                .map(ScholarRankOptionDTO::from)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 分页查询学者列表（支持 keyword、areaId、rank 筛选）
     */
    @GetMapping("/scholar/page")
    public Result<IPage<TeamBasicScholarRespDTO>> pageScholar(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) Integer rank) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        return Result.success(scholarService.pageScholar(pageNum, pageSize, keyword, areaId, rank));
    }

    /**
     * 获取学者详细信息
     * @param id
     * @return
     */
    @GetMapping("/scholar/details/{id}")
    public Result<TeamScholarDetailsRespDTO> getScholarDetails(@PathVariable int id){
        return Result.success(scholarService.getScholarDetails(id));
    }

    /**
     * 新增学者
     * @param requestParam
     * @return
     */
    @PostMapping("/scholar/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result addScholar(@RequestBody TeamAddScholarReqDTO requestParam){
        scholarService.addScholar(requestParam);
        return Result.success();
    }

    /**
     * 修改学者信息
     * @param requestParam
     * @return
     */
    @PutMapping("/scholar/update")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "scholar", idSpel = "#requestParam.id")
    public Result updateScholar(@RequestBody TeamUpdateScholarReqDTO requestParam){
        scholarService.updateScholar(requestParam);
        return Result.success();
    }

    /**
     * 删除学者
     * @param id
     * @return
     */
    @DeleteMapping("/scholar/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "scholar", idSpel = "#id")
    public Result deleteScholarById(@PathVariable int id){
        scholarService.deleteScholarById(id);
        return Result.success();
    }



}
