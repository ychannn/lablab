package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.PaperPublicationAddReqDTO;
import org.ychan.lablab.dto.req.PaperPublicationUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.PaperPublicationRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.science.PaperPublication;
import org.ychan.lablab.mapper.PaperPublicationMapper;
import org.ychan.lablab.service.PaperPublicationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperPublicationServiceImpl extends ServiceImpl<PaperPublicationMapper, PaperPublication> implements PaperPublicationService {

    private final PaperPublicationMapper paperPublicationMapper;

    /**
     * 获取所有论文发表列表
     * @return
     */
    @Override
    public List<PaperPublicationRespDTO> listPaperPublication() {
        List<PaperPublication> publicationList = lambdaQuery()
                .eq(PaperPublication::getDeleted, CommonConstants.FALSE)
                .orderByDesc(PaperPublication::getCreateTime)
                .list();
        return publicationList.stream()
                .map(publication -> {
                    PaperPublicationRespDTO respDTO = new PaperPublicationRespDTO();
                    BeanUtils.copyProperties(publication, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取论文发表详情
     * @param id
     * @return
     */
    @Override
    public PaperPublicationRespDTO getPaperPublicationById(Integer id) {
        PaperPublication publication = paperPublicationMapper.selectById(id);
        if (publication == null || publication.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("论文发表不存在");
        }
        PaperPublicationRespDTO respDTO = new PaperPublicationRespDTO();
        BeanUtils.copyProperties(publication, respDTO);
        return respDTO;
    }

    /**
     * 新增论文发表
     * @param requestParam
     */
    @Override
    @Transactional
    public void addPaperPublication(PaperPublicationAddReqDTO requestParam) {
        PaperPublication publication = new PaperPublication();
        BeanUtils.copyProperties(requestParam, publication);
        int insert = paperPublicationMapper.insert(publication);
        if (insert < 1) {
            throw new BusinessException("新增论文发表失败");
        }
    }

    /**
     * 修改论文发表
     * @param requestParam
     */
    @Override
    @Transactional
    public void updatePaperPublication(PaperPublicationUpdateReqDTO requestParam) {
        PaperPublication publication = paperPublicationMapper.selectById(requestParam.getId());
        if (publication == null || publication.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("论文发表不存在");
        }

        BeanUtils.copyProperties(requestParam, publication);
        int update = paperPublicationMapper.updateById(publication);
        if (update < 1) {
            throw new BusinessException("修改论文发表失败");
        }
    }

    /**
     * 删除论文发表
     * @param id
     */
    @Override
    @Transactional
    public void deletePaperPublicationById(Integer id) {
        PaperPublication publication = paperPublicationMapper.selectById(id);
        if (publication == null || publication.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("论文发表不存在");
        }
        publication.setDeleted(CommonConstants.TRUE);
        paperPublicationMapper.updateById(publication);
    }
}

