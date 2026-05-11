package org.ychan.lablab.service;

import org.ychan.lablab.dto.resp.search.SearchRespDTO;

public interface SearchService {

    SearchRespDTO search(String keyword, int page, int size);
}
