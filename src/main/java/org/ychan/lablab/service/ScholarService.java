package org.ychan.lablab.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ychan.lablab.dto.req.TeamAddScholarReqDTO;
import org.ychan.lablab.dto.req.TeamUpdateScholarReqDTO;
import org.ychan.lablab.dto.resp.team.TeamScholarDetailsRespDTO;
import org.ychan.lablab.dto.resp.team.TeamBasicScholarRespDTO;
import org.ychan.lablab.entity.team.Scholar;

import java.util.List;

public interface ScholarService extends IService<Scholar> {

    /**
     * 返回简单学者列表（门户用）
     */
    List<TeamBasicScholarRespDTO> listBasicScholar();

    /**
     * 分页查询学者列表（后台用）
     */
    IPage<TeamBasicScholarRespDTO> pageScholar(int pageNum, int pageSize);

    /**
     * 返回学者详情
     * @param id
     * @return
     */
    TeamScholarDetailsRespDTO getScholarDetails(int id);

    /**
     * 新增学者
     * @param requestParam
     */
    void addScholar(TeamAddScholarReqDTO requestParam);

    /**
     * 修改学者
     * @param requestParam
     */
    void updateScholar(TeamUpdateScholarReqDTO requestParam);

    /**
     * 删除学者
     * @param id
     */
    void deleteScholarById(int id);
}
