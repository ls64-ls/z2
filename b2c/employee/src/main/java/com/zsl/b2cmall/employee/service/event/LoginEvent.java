package com.zsl.b2cmall.employee.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginEvent implements Serializable {

    private Integer employeeId;
    private Integer shopId;
    private String username;
    private String loginIp;
    private String loginDevice;
    private String loginLocation;
    private Date loginTime;
}
