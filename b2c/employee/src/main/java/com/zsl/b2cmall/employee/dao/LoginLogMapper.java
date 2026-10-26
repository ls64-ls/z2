package com.zsl.b2cmall.employee.dao;

import com.zsl.b2cmall.employee.entity.LoginLogPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface LoginLogMapper {

    @Insert("INSERT INTO tb_login_logs (user_id, login_ip, login_device, login_location, login_time, user_agent, status) " +
            "VALUES (#{userId}, #{loginIp}, #{loginDevice}, #{loginLocation}, #{loginTime}, #{userAgent}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LoginLogPO loginLog);
}
