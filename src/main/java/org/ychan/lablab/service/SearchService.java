package org.ychan.lablab.service;

import org.ychan.lablab.document.SearchDocument;
import org.ychan.lablab.dto.resp.search.SearchRespDTO;

import java.util.List;

/**
 * 搜索服务
 */
public interface SearchService {

    /**
     * 搜索
     */
    SearchRespDTO search(String keyword, int page, int size);

    /**
     * 同步数据到Elasticsearch
     */
    void syncData();

    /**
     * 添加单个文档
     */
    void addDocument(SearchDocument document);

    /**
     * 删除单个文档
     */
    void deleteDocument(String id);

    /**
     * 批量添加文档
     */
    void addDocuments(List<SearchDocument> documents);
}
