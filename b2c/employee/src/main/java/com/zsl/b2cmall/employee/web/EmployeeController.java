package com.zsl.b2cmall.employee.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.employee.request.AddEmployeeRequestVO;
import com.zsl.b2cmall.employee.request.LoginRequestVO;
import com.zsl.b2cmall.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/save")
    public BaseResponseVO<Integer> save(@RequestBody @Valid AddEmployeeRequestVO req) {
        Integer id = employeeService.addEmployee(req);
        return BaseResponseVO.success(id);
    }

    @PostMapping("/login")
    public BaseResponseVO<String> login(@RequestBody @Valid LoginRequestVO req) {
        String token = employeeService.login(req);
        return BaseResponseVO.success(token);
    }

    @PostMapping("/checkToken")
    public BaseResponseVO<Boolean> checkToken(@RequestParam("token") String token) {
        boolean b = employeeService.checkToken(token);
        return BaseResponseVO.success(b);
    }
}
