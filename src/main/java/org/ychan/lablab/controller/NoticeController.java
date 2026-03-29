package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.config.lock.EntityLock;
import org.ychan.lablab.dto.req.NoticeAddReqDTO;
import org.ychan.lablab.dto.req.NoticeUpdateReqDTO;
import org.ychan.lablab.dto.resp.news.NoticeRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.NoticeService;

import java.util.List;

/**
 * 公告（通知）接口
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController extends BaseController {

    private final NoticeService noticeService;

    /**
     * 获取所有公告列表（前台，简单列表）
     */
    @GetMapping("/list")
    public Result<List<NoticeRespDTO>> listNotice() {
        return Result.success(noticeService.listNotice());
    }

    /**
     * 分页获取公告（前台，支持关键词、时间范围筛选）
     */
    @GetMapping("/page")
    public Result<IPage<NoticeRespDTO>> pageNotice(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String timeStart,
            @RequestParam(required = false) String timeEnd) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<NoticeRespDTO> page = noticeService.pageNotice(pageNum, pageSize, keyword, timeStart, timeEnd);
        return Result.success(page);
    }

    /**
     * 根据 id 获取公告详情（前台）
     */
    @GetMapping("/{id}")
    public Result<NoticeRespDTO> getNoticeById(@PathVariable Integer id) {
        return Result.success(noticeService.getNoticeById(id));
    }

    /**
     * 新增公告（后台，需登录）
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN, RoleEnum.OPERATOR})
    public Result<Void> addNotice(@RequestBody NoticeAddReqDTO requestParam) {
        noticeService.addNotice(requestParam);
        return Result.success();
    }

    /**
     * 修改公告（后台，需登录）
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN, RoleEnum.OPERATOR})
    @EntityLock(prefix = "notice", idSpel = "#requestParam.id")
    public Result<Void> updateNotice(@RequestBody NoticeUpdateReqDTO requestParam) {
        noticeService.updateNotice(requestParam);
        return Result.success();
    }

    /**
     * 删除公告（后台，需登录）
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN, RoleEnum.OPERATOR})
    @EntityLock(prefix = "notice", idSpel = "#id")
    public Result<Void> deleteNoticeById(@PathVariable Integer id) {
        noticeService.deleteNoticeById(id);
        return Result.success();
    }
}
