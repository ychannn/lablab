package org.ychan.lablab.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.ResearchDirectionAddReqDTO;
import org.ychan.lablab.dto.req.ResearchDirectionUpdateReqDTO;
import org.ychan.lablab.dto.resp.research.ResearchDirectionRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.science.ResearchDirection;
import org.ychan.lablab.mapper.ResearchDirectionMapper;
import org.ychan.lablab.service.ResearchDirectionService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResearchDirectionServiceImpl extends ServiceImpl<ResearchDirectionMapper, ResearchDirection> implements ResearchDirectionService {

    private final ResearchDirectionMapper researchDirectionMapper;

    /**
     * 获取所有研究方向列表
     * @return
     */
    @Override
    public List<ResearchDirectionRespDTO> listResearchDirection() {
        List<ResearchDirection> directionList = lambdaQuery()
                .eq(ResearchDirection::getDeleted, CommonConstants.FALSE)
                .orderByDesc(ResearchDirection::getCreateTime)
                .list();
        return directionList.stream()
                .map(direction -> {
                    ResearchDirectionRespDTO respDTO = new ResearchDirectionRespDTO();
                    BeanUtils.copyProperties(direction, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public IPage<ResearchDirectionRespDTO> pageResearchDirection(int pageNum, int pageSize) {
        IPage<ResearchDirection> page = lambdaQuery()
                .eq(ResearchDirection::getDeleted, CommonConstants.FALSE)
                .orderByDesc(ResearchDirection::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
        IPage<ResearchDirectionRespDTO> result = page.convert(entity -> {
            ResearchDirectionRespDTO researchDirectionRespDTO = new ResearchDirectionRespDTO();
            BeanUtils.copyProperties(entity, researchDirectionRespDTO);
            return researchDirectionRespDTO;
        });

        return result;
    }

    /**
     * 根据id获取研究方向详情
     * @param id
     * @return
     */
    @Override
    public ResearchDirectionRespDTO getResearchDirectionById(Integer id) {
        ResearchDirection direction = researchDirectionMapper.selectById(id);
        if (direction == null || direction.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("研究方向不存在");
        }
        ResearchDirectionRespDTO respDTO = new ResearchDirectionRespDTO();
        BeanUtils.copyProperties(direction, respDTO);
        return respDTO;
    }

    /**
     * 新增研究方向
     * @param requestParam
     */
    @Override
    @Transactional
    public void addResearchDirection(ResearchDirectionAddReqDTO requestParam) {
        ResearchDirection titleCheck = researchDirectionMapper.selectOne(new LambdaQueryWrapper<>(ResearchDirection.class)
                .eq(ResearchDirection::getTitle, requestParam.getTitle())
                .eq(ResearchDirection::getDeleted, CommonConstants.FALSE));
        if(ObjectUtils.isNotNull(titleCheck) && requestParam.getTitle().equals(titleCheck.getTitle())){
            throw new BusinessException("研究方向名称不能相同");
        }
        ResearchDirection direction = new ResearchDirection();
        BeanUtils.copyProperties(requestParam, direction);
        int insert = researchDirectionMapper.insert(direction);
        if (insert < 1) {
            throw new BusinessException("新增研究方向失败");
        }
    }

    /**
     * 修改研究方向
     * @param requestParam
     */
    @Override
    @Transactional
    public void updateResearchDirection(ResearchDirectionUpdateReqDTO requestParam) {
        ResearchDirection direction = researchDirectionMapper.selectById(requestParam.getId());
        if (direction == null || direction.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("研究方向不存在");
        }

        // 检查标题是否与其他记录重复
        ResearchDirection titleCheck = researchDirectionMapper.selectOne(new LambdaQueryWrapper<>(ResearchDirection.class)
                .eq(ResearchDirection::getTitle, requestParam.getTitle())
                .eq(ResearchDirection::getDeleted, CommonConstants.FALSE)
                .ne(ResearchDirection::getId, requestParam.getId()));
        if(ObjectUtils.isNotNull(titleCheck)){
            throw new BusinessException("研究方向名称不能相同");
        }

        BeanUtils.copyProperties(requestParam, direction);
        int update = researchDirectionMapper.updateById(direction);
        if (update < 1) {
            throw new BusinessException("修改研究方向失败");
        }
    }

    /**
     * 删除研究方向
     * @param id
     */
    @Override
    @Transactional
    public void deleteResearchDirectionById(Integer id) {
        ResearchDirection direction = researchDirectionMapper.selectById(id);
        if (direction == null || direction.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("研究方向不存在");
        }
        direction.setDeleted(CommonConstants.TRUE);
        researchDirectionMapper.updateById(direction);
    }
}

