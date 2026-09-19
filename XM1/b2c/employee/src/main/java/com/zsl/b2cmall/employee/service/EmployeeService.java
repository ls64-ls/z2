package com.zsl.b2cmall.employee.service;

import com.zsl.b2cmall.employee.request.AddEmployeeRequestVO;
import com.zsl.b2cmall.employee.request.LoginRequestVO;

public interface EmployeeService {

    Integer addEmployee(AddEmployeeRequestVO vo);

    String login(LoginRequestVO req);

    boolean checkToken(String token);
}
