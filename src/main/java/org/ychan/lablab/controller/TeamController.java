package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.dto.req.TeamAddScholarReqDTO;
import org.ychan.lablab.dto.req.TeamUpdateScholarReqDTO;
import org.ychan.lablab.dto.resp.team.TeamScholarDetailsRespDTO;
import org.ychan.lablab.dto.resp.team.TeamBasicScholarRespDTO;
import org.ychan.lablab.service.ScholarService;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final ScholarService scholarService;
    /**
     * 返回学者基础信息列表
     */
    @GetMapping("/scholar/basic-list")
    public List<TeamBasicScholarRespDTO> listBasicScholar(){
        return scholarService.listBasicScholar();
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
    public Result deleteScholarById(@PathVariable int id){
        scholarService.deleteScholarById(id);
        return Result.success();
    }



}
