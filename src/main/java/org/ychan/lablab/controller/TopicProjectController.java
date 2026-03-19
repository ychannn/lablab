package org.ychan.lablab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.config.RequiredRole;
import org.ychan.lablab.dto.req.TopicProjectAddReqDTO;
import org.ychan.lablab.dto.req.TopicProjectUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.TopicProjectRespDTO;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.TopicProjectService;

import java.util.List;

@RestController
@RequestMapping("/topic-project")
@RequiredArgsConstructor
public class TopicProjectController extends BaseController {

    private final TopicProjectService topicProjectService;

    /**
     * 获取所有课题项目列表
     * @return
     */
    @GetMapping("/list")
    public Result<List<TopicProjectRespDTO>> listTopicProject() {
        return Result.success(topicProjectService.listTopicProject());
    }

    /**
     * 分页获取所有课题项目列表
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<TopicProjectRespDTO>> pageTopicProject(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTimeStart,
            @RequestParam(required = false) String startTimeEnd,
            @RequestParam(required = false) String endTimeStart,
            @RequestParam(required = false) String endTimeEnd) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(Math.min(pageSize, 50), 1);
        IPage<TopicProjectRespDTO> list = topicProjectService.pageTopicProject(pageNum, pageSize, keyword, startTimeStart, startTimeEnd, endTimeStart, endTimeEnd);
        return Result.success(list);
    }

    /**
     * 根据id获取课题项目详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<TopicProjectRespDTO> getTopicProjectById(@PathVariable Integer id) {
        return Result.success(topicProjectService.getTopicProjectById(id));
    }

    /**
     * 新增课题项目
     * @param requestParam
     * @return
     */
    @PostMapping("/add")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> addTopicProject(@RequestBody TopicProjectAddReqDTO requestParam) {
        topicProjectService.addTopicProject(requestParam);
        return Result.success();
    }

    /**
     * 修改课题项目
     * @param requestParam
     * @return
     */
    @PutMapping("/update")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> updateTopicProject(@RequestBody TopicProjectUpdateReqDTO requestParam) {
        topicProjectService.updateTopicProject(requestParam);
        return Result.success();
    }

    /**
     * 删除课题项目
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @RequiredRole({RoleEnum.ADMIN})
    public Result<Void> deleteTopicProjectById(@PathVariable Integer id) {
        topicProjectService.deleteTopicProjectById(id);
        return Result.success();
    }
}

