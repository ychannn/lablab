package org.ychan.lablab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ychan.lablab.common.constant.CommonConstants;
import org.ychan.lablab.dto.req.TeamAddScholarReqDTO;
import org.ychan.lablab.dto.req.TeamUpdateScholarReqDTO;
import org.ychan.lablab.dto.resp.team.TeamBasicScholarRespDTO;
import org.ychan.lablab.dto.resp.team.TeamScholarDetailsRespDTO;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.team.Intro;
import org.ychan.lablab.entity.team.Paper;
import org.ychan.lablab.entity.team.Project;
import org.ychan.lablab.entity.team.Scholar;
import org.ychan.lablab.mapper.*;
import org.ychan.lablab.service.IntroService;
import org.ychan.lablab.service.PaperService;
import org.ychan.lablab.service.ProjectService;
import org.ychan.lablab.service.ScholarService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScholarServiceImpl extends ServiceImpl<ScholarMapper, Scholar> implements ScholarService {

    // service
    private final IntroService introService;

    private final PaperService paperService;

    private final ProjectService projectService;
    // mapper
    private final PaperMapper paperMapper;

    private final ProjectMapper projectMapper;

    private final IntroMapper introMapper;

    private final ScholarMapper scholarMapper;
    /**
     * 返回简单教师列表
     * @return
     */
    public List<TeamBasicScholarRespDTO> listBasicScholar(){
        List<Scholar> scholarList = baseMapper.selectList(new LambdaQueryWrapper<>(Scholar.class)
                .eq(Scholar::getDeleted, 0));
        return scholarList.stream()
                .map(this::toBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<TeamBasicScholarRespDTO> pageScholar(int pageNum, int pageSize) {
        IPage<Scholar> page = baseMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<>(Scholar.class).eq(Scholar::getDeleted, 0));
        return page.convert(this::toBasicDto);
    }

    private TeamBasicScholarRespDTO toBasicDto(Scholar s) {
        TeamBasicScholarRespDTO dto = new TeamBasicScholarRespDTO();
        dto.setId(s.getId());
        dto.setArea(s.getAreaId());
        dto.setName(s.getName());
        dto.setRank(s.getTitle());
        dto.setPhoto(s.getPhoto());
        return dto;
    }

    /**
     * 根据id返回学者详细信息
     * @param id
     * @return
     */
    @Override
    public TeamScholarDetailsRespDTO getScholarDetails(int id) {
        Scholar scholar = baseMapper.selectById(id);
        if (scholar == null || scholar.getDeleted() == CommonConstants.TRUE) {
            throw new BusinessException("学者不存在");
        }
        TeamScholarDetailsRespDTO resp = new TeamScholarDetailsRespDTO();
        BeanUtils.copyProperties(scholar, resp);
        List<Intro> introList = introMapper.selectList(new LambdaQueryWrapper<>(Intro.class)
                .eq(Intro::getScholarId, id));
        List<Paper> paperList = paperMapper.selectList(new LambdaQueryWrapper<>(Paper.class)
                .eq(Paper::getScholarId, id));
        List<Project> projectList = projectMapper.selectList(new LambdaQueryWrapper<>(Project.class)
                .eq(Project::getScholarId, id));
        resp.setIntro(introList);
        resp.setPaperList(paperList);
        resp.setProjectList(projectList);
        return resp;
    }

    /**
     * 新增学者
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addScholar(TeamAddScholarReqDTO requestParam){
        Scholar scholar = new Scholar();
        BeanUtils.copyProperties(requestParam, scholar);
        scholar.setTitle(requestParam.getRank());
        scholar.setAreaId(requestParam.getAreaId());
        int insert = scholarMapper.insert(scholar);
        if (insert < 1){
            throw new BusinessException("新增学者失败");
        }
        if(CollectionUtils.isNotEmpty(requestParam.getIntroList())){
            requestParam.getIntroList().forEach(
                    each -> each.setScholarId(scholar.getId())
            );
            introService.saveBatch(requestParam.getIntroList());
        }
        if(CollectionUtils.isNotEmpty(requestParam.getPaperList())){
            requestParam.getPaperList().forEach(
                    each -> each.setScholarId(scholar.getId())
            );
            paperService.saveBatch(requestParam.getPaperList());
        }
        if(CollectionUtils.isNotEmpty(requestParam.getProjectList())){
            requestParam.getProjectList().forEach(
                    each -> each.setScholarId(scholar.getId())
            );
            projectService.saveBatch(requestParam.getProjectList());
        }
    }

    /**
     * 修改学者
     * @param requestParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScholar(TeamUpdateScholarReqDTO requestParam){
        Scholar exist = scholarMapper.selectById(requestParam.getId());
        if(ObjectUtils.isNull(exist) || exist.getDeleted() == CommonConstants.TRUE){
            throw new BusinessException("学者不存在");
        }
        Scholar scholar = new Scholar();
        BeanUtils.copyProperties(requestParam, scholar);
        scholar.setTitle(requestParam.getRank());
        scholar.setAreaId(requestParam.getAreaId());
        scholarMapper.updateById(scholar);
        if(CollectionUtils.isNotEmpty(requestParam.getIntroList())){
            introService.updateBatchById(requestParam.getIntroList());
        }
        if(CollectionUtils.isNotEmpty(requestParam.getPaperList())){
            paperService.updateBatchById(requestParam.getPaperList());
        }
        if(CollectionUtils.isNotEmpty(requestParam.getIntroList())){
            introService.updateBatchById(requestParam.getIntroList());
        }
        if(CollectionUtils.isNotEmpty(requestParam.getProjectList())){
            projectService.updateBatchById(requestParam.getProjectList());
        }
    }

    /**
     * 删除学者
     * @param id
     */
    public void deleteScholarById(int id){
        Scholar exist = scholarMapper.selectById(id);
        if(ObjectUtils.isNull(exist) || exist.getDeleted() == CommonConstants.TRUE){
            throw new BusinessException("学者不存在");
        }
        Scholar scholar = new Scholar();
        scholar.setId(id);
        scholar.setDeleted(CommonConstants.TRUE);
        int update = scholarMapper.updateById(scholar);
        if(update < 1){
            throw new BusinessException("删除学者失败");
        }
    }
}
