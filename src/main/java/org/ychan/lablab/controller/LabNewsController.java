package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.LabNewsAddReqDTO;
import org.ychan.lablab.dto.req.LabNewsUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.LabNewsRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.LabNewsService;

import java.util.List;

@RestController
@RequestMapping("/lab-news")
@RequiredArgsConstructor
public class LabNewsController extends BaseController {

    private final LabNewsService labNewsService;

    /**
     * 获取所有实验室新闻列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<LabNewsRespDTO>> listLabNews() {
        return Result.success(labNewsService.listLabNews());
    }

    /**
     * 分页获取所有实验室新闻列表
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<LabNewsRespDTO>> pageLabNews(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String timeStart,
            @RequestParam(required = false) String timeEnd) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<LabNewsRespDTO> list = labNewsService.pageLabNews(pageNum, pageSize, keyword, timeStart, timeEnd);
        return Result.success(list);
    }

    /**
     * 根据id获取实验室新闻详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<LabNewsRespDTO> getLabNewsById(@PathVariable Integer id) {
        return Result.success(labNewsService.getLabNewsById(id));
    }

    /**
     * 新增实验室新闻
     * @param requestParam
     * @return
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addLabNews(@RequestBody LabNewsAddReqDTO requestParam) {
        labNewsService.addLabNews(requestParam);
        return Result.success();
    }

    /**
     * 修改实验室新闻
     * @param requestParam
     * @return
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> updateLabNews(@RequestBody LabNewsUpdateReqDTO requestParam) {
        labNewsService.updateLabNews(requestParam);
        return Result.success();
    }

    /**
     * 删除实验室新闻
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> deleteLabNewsById(@PathVariable Integer id) {
        labNewsService.deleteLabNewsById(id);
        return Result.success();
    }
}


