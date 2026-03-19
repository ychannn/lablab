package org.ychan.lablab.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.ResearchDirectionAddReqDTO;
import org.ychan.lablab.dto.req.ResearchDirectionUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.ResearchDirectionRespDTO;
import org.ychan.lablab.entity.science.ResearchDirection;

import java.util.List;

public interface ResearchDirectionService extends IService<ResearchDirection> {

    /**
     * 获取所有研究方向列表
     * @return
     */
    List<ResearchDirectionRespDTO> listResearchDirection();

    /**
     * 分页获取研究方向（支持关键词筛选）
     * @return
     */
    IPage<ResearchDirectionRespDTO> pageResearchDirection(int pageNum, int pageSize, String keyword);
    /**
     * 根据id获取研究方向详情
     * @param id
     * @return
     */
    ResearchDirectionRespDTO getResearchDirectionById(Integer id);

    /**
     * 新增研究方向
     * @param requestParam
     */
    void addResearchDirection(ResearchDirectionAddReqDTO requestParam);

    /**
     * 修改研究方向
     * @param requestParam
     */
    void updateResearchDirection(ResearchDirectionUpdateReqDTO requestParam);

    /**
     * 删除研究方向
     * @param id
     */
    void deleteResearchDirectionById(Integer id);
}

