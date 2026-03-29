package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.config.lock.EntityLock;
import org.ychan.lablab.dto.req.PaperPublicationAddReqDTO;
import org.ychan.lablab.dto.req.PaperPublicationUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.PaperPublicationRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.PaperPublicationService;

import java.util.List;

@RestController
@RequestMapping("/api/publication")
@RequiredArgsConstructor
public class PaperPublicationController extends BaseController {

    private final PaperPublicationService paperPublicationService;

    /**
     * 获取所有论文发表列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<PaperPublicationRespDTO>> listPaperPublication() {
        return Result.success(paperPublicationService.listPaperPublication());
    }

    /**
     * 分页获取所有论文发表列表
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<PaperPublicationRespDTO>> pagePaperPublication(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String publishTimeStart,
            @RequestParam(required = false) String publishTimeEnd) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<PaperPublicationRespDTO> list = paperPublicationService.pagePaperPublication(pageNum, pageSize, keyword, publishTimeStart, publishTimeEnd);
        return Result.success(list);
    }

    /**
     * 根据id获取论文发表详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<PaperPublicationRespDTO> getPaperPublicationById(@PathVariable Integer id) {
        return Result.success(paperPublicationService.getPaperPublicationById(id));
    }

    /**
     * 新增论文发表
     * @param requestParam
     * @return
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addPaperPublication(@RequestBody PaperPublicationAddReqDTO requestParam) {
        paperPublicationService.addPaperPublication(requestParam);
        return Result.success();
    }

    /**
     * 修改论文发表
     * @param requestParam
     * @return
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "publication", idSpel = "#requestParam.id")
    public Result<Void> updatePaperPublication(@RequestBody PaperPublicationUpdateReqDTO requestParam) {
        paperPublicationService.updatePaperPublication(requestParam);
        return Result.success();
    }

    /**
     * 删除论文发表
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    @EntityLock(prefix = "publication", idSpel = "#id")
    public Result<Void> deletePaperPublicationById(@PathVariable Integer id) {
        paperPublicationService.deletePaperPublicationById(id);
        return Result.success();
    }
}

