package org.ychan.lablab.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.ychan.lablab.entity.team.Area;

public interface AreaMapper extends BaseMapper<Area> {

    @Select("select IFNULL(MAX(sort), 0) max_sort from t_area where deleted = 0")
    Integer selectMaxSort();
}
