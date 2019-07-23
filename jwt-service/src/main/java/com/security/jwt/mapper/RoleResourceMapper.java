package com.security.jwt.mapper;


import com.security.jwt.po.MenuPo;
import com.security.jwt.po.RoleResourcePo;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @author miou
 * @date 2019-05-08
 */
public interface RoleResourceMapper extends Mapper<RoleResourcePo> {

    List<MenuPo> getMenuResourceByUserName(String userName);
}
