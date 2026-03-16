package org.ychan.lablab.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.ychan.lablab.document.SearchDocument;

/**
 * 搜索仓库
 */
public interface SearchRepository extends ElasticsearchRepository<SearchDocument, String> {
}
