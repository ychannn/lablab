package org.ychan.lablab.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ychan.lablab.common.result.Result;
import org.ychan.lablab.dto.resp.search.SearchRespDTO;
import org.ychan.lablab.service.SearchService;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController extends BaseController {

    private final SearchService searchService;

    /**
     * 搜索接口
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     */
    @GetMapping("/query")
    public Result<SearchRespDTO> search(@RequestParam String keyword, 
                                      @RequestParam(defaultValue = "1") int page, 
                                      @RequestParam(defaultValue = "10") int size) {
        SearchRespDTO result = searchService.search(keyword, page, size);
        return Result.success(result);
    }

    /**
     * 同步数据到Elasticsearch
     * 管理员接口
     */
    @GetMapping("/admin/sync")
    public Result<Void> syncData() {
        searchService.syncData();
        return Result.success();
    }
}
