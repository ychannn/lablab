package org.ychan.lablab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ychan.lablab.entity.config.FriendLink;

import java.util.List;

/**
 * 友情链接 Mapper
 */
@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {

    /**
     * 获取启用的友情链接（按排序倒序）
     */
    List<FriendLink> selectEnabledLinks();
}