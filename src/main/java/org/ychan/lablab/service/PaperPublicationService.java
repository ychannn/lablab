package org.ychan.lablab.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.PaperPublicationAddReqDTO;
import org.ychan.lablab.dto.req.PaperPublicationUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.PaperPublicationRespDTO;
import org.ychan.lablab.entity.science.PaperPublication;

import java.util.List;

public interface PaperPublicationService extends IService<PaperPublication> {

    /**
     * 获取所有论文发表列表
     * @return
     */
    List<PaperPublicationRespDTO> listPaperPublication();

    /**
     * 分页获取论文发表
     */
    IPage<PaperPublicationRespDTO> pagePaperPublication(int pageNum, int pageSize);

    /**
     * 根据id获取论文发表详情
     * @param id
     * @return
     */
    PaperPublicationRespDTO getPaperPublicationById(Integer id);

    /**
     * 新增论文发表
     * @param requestParam
     */
    void addPaperPublication(PaperPublicationAddReqDTO requestParam);

    /**
     * 修改论文发表
     * @param requestParam
     */
    void updatePaperPublication(PaperPublicationUpdateReqDTO requestParam);

    /**
     * 删除论文发表
     * @param id
     */
    void deletePaperPublicationById(Integer id);
}

