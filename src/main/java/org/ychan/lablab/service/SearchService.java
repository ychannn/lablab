package org.ychan.lablab.service;

// import org.ychan.lablab.document.SearchDocument;
import org.ychan.lablab.dto.resp.search.SearchRespDTO;

// import java.util.List;

/**
 * 搜索服务（Elasticsearch 已禁用，使用数据库搜索）
 */
public interface SearchService {

    /**
     * 搜索
     */
    SearchRespDTO search(String keyword, int page, int size);

    /**
     * 同步数据到Elasticsearch（已禁用）
     */
    default void syncData() {
        // Elasticsearch 已禁用
    }

    /**
     * 添加单个文档（已禁用）
     */
    default void addDocument(Object document) {
        // Elasticsearch 已禁用
    }

    /**
     * 删除单个文档（已禁用）
     */
    default void deleteDocument(String id) {
        // Elasticsearch 已禁用
    }

    /**
     * 批量添加文档（已禁用）
     */
    default void addDocuments(Object documents) {
        // Elasticsearch 已禁用
    }
}
