package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.config.lock.EntityLock;
import org.ychan.lablab.dto.req.AreaAddReqDTO;
import org.ychan.lablab.dto.req.AreaUpdateReqDTO;
import org.ychan.lablab.dto.req.AreaSortReqDTO;
import org.ychan.lablab.dto.resp.AreaRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.AreaService;

import java.util.List;

@RestController
@RequestMapping("/api/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    /**
     * 获取所有领域列表（可选 keyword 按标题模糊筛选）
     * @return
     */
    @GetMapping("/list")
    public Result<List<AreaRespDTO>> listArea(@RequestParam(required = false) String keyword) {
        return Result.success(areaService.listArea(keyword));
    }

    /**
     * 根据id获取领域详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AreaRespDTO> getAreaById(@PathVariable int id) {
        return Result.success(areaService.getAreaById(id));
    }

    /**
     * 新增领域
     * @param requestParam
     * @return
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addArea(@RequestBody AreaAddReqDTO requestParam) {
        areaService.addArea(requestParam);
        return Result.success();
    }

    /**
     * 修改领域
     * @param requestParam
     * @return
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "area", idSpel = "#requestParam.id")
    public Result<Void> updateArea(@RequestBody AreaUpdateReqDTO requestParam) {
        areaService.updateArea(requestParam);
        return Result.success();
    }

    /**
     * 删除领域
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "area", idSpel = "#id")
    public Result<Void> deleteAreaById(@PathVariable int id) {
        areaService.deleteAreaById(id);
        return Result.success();
    }


    /**
     * 领域排序
     * @param sortList
     * @return
     */
    @PostMapping("/sort")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> sort(@RequestBody List<AreaSortReqDTO> sortList){
        areaService.sort(sortList);
        return Result.success();
    }
}

