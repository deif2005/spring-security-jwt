package com.security.jwt.service;

import com.github.pagehelper.PageInfo;
import com.security.jwt.po.ActivatePo;
import com.security.jwt.po.OrderConfigPo;
import com.security.jwt.query.ActivateMachineQuery;
import com.security.jwt.query.OrderConfigQuery;

/**
 * @author miou
 * @date 2019-04-17
 */
public interface IOrderConfigService {

    void importOrderConfigInfo(String filePath);

    OrderConfigPo getOrderConfig(String id);

    PageInfo<OrderConfigPo> listOrderConfig(OrderConfigQuery orderConfigQuery);

    void modifyOrderConfig(OrderConfigPo orderConfigPo);

    String checkActivate(ActivatePo activatePo);

    PageInfo<ActivatePo> listActivateMachine(ActivateMachineQuery activateMachineQuery);

    String testSelect();
}
