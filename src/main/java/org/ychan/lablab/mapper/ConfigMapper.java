package org.ychan.lablab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.ychan.lablab.entity.config.Config;

/**
 * 系统配置 Mapper
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 根据配置类型查询配置
     */
    @Select("SELECT * FROM t_config WHERE config_type = #{configType} AND deleted = 0 LIMIT 1")
    Config selectByType(@Param("configType") String configType);
}
