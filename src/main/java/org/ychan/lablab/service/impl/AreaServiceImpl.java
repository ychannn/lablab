package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.AreaAddReqDTO;
import org.ychan.lablab.dto.req.AreaUpdateReqDTO;
import org.ychan.lablab.dto.req.AreaSortReqDTO;
import org.ychan.lablab.dto.resp.AreaRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.team.Area;
import org.ychan.lablab.entity.team.BaseEntity;
import org.ychan.lablab.entity.team.Scholar;
import org.ychan.lablab.mapper.AreaMapper;
import org.ychan.lablab.mapper.ScholarMapper;
import org.ychan.lablab.service.AreaService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    private final RedissonClient redissonClient;

    private final AreaMapper areaMapper;

    private final ScholarMapper scholarMapper;
    /**
     * 获取所有领域列表
     * @return
     */
    @Override
    public List<AreaRespDTO> listArea() {
        List<Area> areaList = lambdaQuery()
                .eq(Area::getDeleted, 0)
                .orderByDesc(Area::getSort)
                .orderByDesc(Area::getUpdateTime)
                .list();
        return areaList.stream()
                .map(area -> {
                    AreaRespDTO respDTO = new AreaRespDTO();
                    BeanUtils.copyProperties(area, respDTO);
                    return respDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据id获取领域详情
     * @param id
     * @return
     */
    @Override
    public AreaRespDTO getAreaById(int id) {
        Area area = areaMapper.selectById(id);
        if (area == null || area.getDeleted() == 1) {
            throw new BusinessException("领域不存在");
        }
        AreaRespDTO respDTO = new AreaRespDTO();
        BeanUtils.copyProperties(area, respDTO);
        return respDTO;
    }

    /**
     * 新增领域
     * @param requestParam
     */
    @Override
    @Transactional
    public void addArea(AreaAddReqDTO requestParam) {
        Area titleCheck = areaMapper.selectOne(new LambdaQueryWrapper<>(Area.class)
                .eq(Area::getTitle, requestParam.getTitle())
                .eq(BaseEntity::getDeleted, CommonConstants.FALSE));
        if(ObjectUtils.isNotNull(titleCheck) && requestParam.getTitle().equals(titleCheck.getTitle())){
            throw new BusinessException("领域名称不能相同");
        }
        Area area = new Area();
        BeanUtils.copyProperties(requestParam, area);
        RLock lock = redissonClient.getLock("area_getMaxSort_lock");
        try{
            lock.lock(30, TimeUnit.SECONDS);
            area.setSort(areaMapper.selectMaxSort() + 1);
            int insert = areaMapper.insert(area);
            if (insert < 1) {
                throw new BusinessException("新增领域失败");
            }

        }
        catch (Exception e){
            log.error("排序错误, 原因：{}", e);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * 修改领域
     * @param requestParam
     */
    @Override
    public void updateArea(AreaUpdateReqDTO requestParam) {
        Area area = areaMapper.selectById(requestParam.getId());
        if (area == null || area.getDeleted() == 1) {
            throw new BusinessException("领域不存在");
        }

        BeanUtils.copyProperties(requestParam, area);
        int update = areaMapper.updateById(area);
        if (update < 1) {
            throw new BusinessException("修改领域失败");
        }
    }

    /**
     * 删除领域
     * @param id
     */
    @Override
    public void deleteAreaById(int id) {
        Area area = areaMapper.selectById(id);
        if (area == null || area.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("领域不存在");
        }
        Long count = scholarMapper.selectCount(new LambdaQueryWrapper<>(Scholar.class)
                .eq(Scholar::getAreaId, id)
                .eq(BaseEntity::getDeleted, CommonConstants.FALSE));
        if(count > 0){
            throw new BusinessException("该领域还存在学者绑定");
        }
        area.setDeleted(CommonConstants.TRUE);
        areaMapper.updateById(area);
    }

    /**
     * 修改排序
     * @param sortList
     */
    @Transactional
    public void sort(List<AreaSortReqDTO> sortList){
        RLock lock = redissonClient.getLock("Area_updateSort_lock");
        HashSet<Integer> hashSet = new HashSet<>();
        sortList.forEach(each -> hashSet.add(each.getSort()));
        if(hashSet.size() != sortList.size()){
            throw new BusinessException("排序值重复");
        }
        Long count = lambdaQuery().eq(BaseEntity::getDeleted, CommonConstants.FALSE)
                .count();
        if(sortList.size() != count){
            throw new BusinessException("排序列表错误");
        }
        Integer maxSort = areaMapper.selectMaxSort();
        maxSort = maxSort == null? 0 : maxSort;
        int max = maxSort;
        try{
            lock.lock();
            List<Area> areaList = sortList.stream()
                    .map(each -> {
                        Area area = new Area();
                        BeanUtils.copyProperties(each, area);
                        area.setSort(max + area.getSort());
                        return area;
                    })
                    .collect(Collectors.toList());
            updateBatchById(areaList);
            areaList.forEach(each -> each.setSort(each.getSort() - max));
            updateBatchById(areaList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }
}
