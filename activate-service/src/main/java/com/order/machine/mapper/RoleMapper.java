package com.order.machine.mapper;

import com.order.machine.po.MenuPo;
import com.order.machine.po.RolePo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author miou
 * @date 2019-05-08
 */
public interface RoleMapper extends Mapper<RolePo>{

    List<RolePo> getRoleByUserName(String userName);

    List<RolePo> getRoleByResource(@Param("resourceId") Integer resourceId);

    List<MenuPo> getMenuResourceByUserName(String userName);
}
