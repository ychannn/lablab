package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.PageResult;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.ResearchDirectionAddReqDTO;
import org.ychan.lablab.dto.req.ResearchDirectionUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.ResearchDirectionRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.ResearchDirectionService;

import java.util.List;

@RestController
@RequestMapping("/research")
@RequiredArgsConstructor
public class ResearchDirectionController extends BaseController{

    private final ResearchDirectionService researchDirectionService;

    /**
     * 获取所有研究方向列表
     * @return
     */
    @GetMapping("/direction/list")
    public Result<List<ResearchDirectionRespDTO>> listResearchDirection() {
        return Result.success(researchDirectionService.listResearchDirection());
    }

    /**
     * 分页获取所有研究方向列表
     * @return
     */
    @GetMapping("/direction/page")
    public Result<IPage<ResearchDirectionRespDTO>> pageResearchDirection(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword
    ) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<ResearchDirectionRespDTO> list = researchDirectionService.pageResearchDirection(pageNum, pageSize, keyword);
        return Result.success(list);
    }

    /**
     * 根据id获取研究方向详情
     * @param id
     * @return
     */
    @GetMapping("/direction/{id}")
    public Result<ResearchDirectionRespDTO> getResearchDirectionById(@PathVariable Integer id) {
        return Result.success(researchDirectionService.getResearchDirectionById(id));
    }

    /**
     * 新增研究方向
     * @param requestParam
     * @return
     */
    @PostMapping("/direction/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addResearchDirection(@RequestBody ResearchDirectionAddReqDTO requestParam) {
        researchDirectionService.addResearchDirection(requestParam);
        return Result.success();
    }

    /**
     * 修改研究方向
     * @param requestParam
     * @return
     */
    @PutMapping("/direction/update")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> updateResearchDirection(@RequestBody ResearchDirectionUpdateReqDTO requestParam) {
        researchDirectionService.updateResearchDirection(requestParam);
        return Result.success();
    }

    /**
     * 删除研究方向
     * @param id
     * @return
     */
    @DeleteMapping("/direction/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> deleteResearchDirectionById(@PathVariable Integer id) {
        researchDirectionService.deleteResearchDirectionById(id);
        return Result.success();
    }
}
