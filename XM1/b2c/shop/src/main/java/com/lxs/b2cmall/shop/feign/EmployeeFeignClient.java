package com.lxs.b2cmall.shop.feign;

import com.lxs.b2cmall.common.response.BaseResponseVO;
import com.lxs.b2cmall.shop.feign.request.AddEmployeeRequestVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "employee-service")
public interface EmployeeFeignClient {

    @PostMapping("/employee/save")
    BaseResponseVO<Integer> save(@RequestBody AddEmployeeRequestVO req);
}
