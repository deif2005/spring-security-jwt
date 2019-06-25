package com.order.machine.po;

import javax.persistence.*;

/**
 * @author miou
 * @date 2019-04-13
 */
@Entity
@Table(name="tb_user")
public class UserPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    //    private String companyId;
    private String userName;
    private String password;
    private String salt;
    private String isLogin;
    private String createTime;

    public UserPo(){

    }

    public UserPo(String userId,String userName){
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
