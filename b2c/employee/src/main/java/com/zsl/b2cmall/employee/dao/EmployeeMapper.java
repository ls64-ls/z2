package com.zsl.b2cmall.employee.dao;

import com.zsl.b2cmall.employee.entity.EmployeePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {

    int save(EmployeePO po);

    EmployeePO getEmployee(@Param("shopId") Integer shopId, @Param("username") String username);

    EmployeePO getById(@Param("id") Integer id);

    int updateLoginInfo(@Param("id") Integer id, @Param("loginCount") Integer loginCount);
}
