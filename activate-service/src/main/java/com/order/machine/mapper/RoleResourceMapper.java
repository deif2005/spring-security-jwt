package com.order.machine.mapper;

import com.order.machine.po.MenuPo;
import com.order.machine.po.RoleResourcePo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author miou
 * @date 2019-05-08
 */
public interface RoleResourceMapper extends Mapper<RoleResourcePo> {

    List<MenuPo> getMenuResourceByUserName(String userName);
}
