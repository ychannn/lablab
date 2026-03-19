package org.ychan.lablab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.AreaAddReqDTO;
import org.ychan.lablab.dto.req.AreaSortReqDTO;
import org.ychan.lablab.dto.req.AreaUpdateReqDTO;
import org.ychan.lablab.dto.resp.AreaRespDTO;
import org.ychan.lablab.entity.team.Area;

import java.util.List;

public interface AreaService extends IService<Area> {

    /**
     * 获取所有领域列表（可选关键词筛选 title）
     * @param keyword 可选，领域名称模糊搜索
     * @return
     */
    List<AreaRespDTO> listArea(String keyword);

    /**
     * 根据id获取领域详情
     * @param id
     * @return
     */
    AreaRespDTO getAreaById(int id);

    /**
     * 新增领域
     * @param requestParam
     */
    void addArea(AreaAddReqDTO requestParam);

    /**
     * 修改领域
     * @param requestParam
     */
    void updateArea(AreaUpdateReqDTO requestParam);

    /**
     * 删除领域
     * @param id
     */
    void deleteAreaById(int id);

    void sort(List<AreaSortReqDTO> sortList);
}
