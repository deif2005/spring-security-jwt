package com.order.machine.po;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author miou
 * @date 2019-05-08
 */
@Table(name="tb_role")
public class RolePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String roleName;
    private String roleNameZh;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleNameZh() {
        return roleNameZh;
    }

    public void setRoleNameZh(String roleNameZh) {
        this.roleNameZh = roleNameZh;
    }
}
