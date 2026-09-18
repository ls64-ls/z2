package com.lxs.b2cmall.employee.service.event.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.lxs.b2cmall.employee.entity.LoginLogPO;
import com.lxs.b2cmall.employee.dao.LoginLogMapper;
import com.lxs.b2cmall.employee.service.event.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 审计日志观察者：记录登录 IP、设备、时间、地点，用于安全审计
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogObserver implements InitializingBean {

    private final EventBus eventBus;
    private final LoginLogMapper loginLogMapper;

    @Subscribe
    public void onLogin(LoginEvent event) {
        log.info("[审计日志] 记录登录日志: employeeId={}, ip={}, device={}, location={}",
                event.getEmployeeId(), event.getLoginIp(), event.getLoginDevice(), event.getLoginLocation());

        LoginLogPO loginLog = new LoginLogPO();
        loginLog.setUserId(event.getEmployeeId());
        loginLog.setLoginIp(event.getLoginIp());
        loginLog.setLoginDevice(event.getLoginDevice());
        loginLog.setLoginLocation(event.getLoginLocation());
        loginLog.setLoginTime(event.getLoginTime() != null ? event.getLoginTime() : new Date());
        loginLog.setUserAgent(event.getLoginDevice());
        loginLog.setStatus(1);

        loginLogMapper.insert(loginLog);
        log.info("[审计日志] 登录日志已记录, loginLogId={}", loginLog.getId());
    }

    @Override
    public void afterPropertiesSet() {
        eventBus.register(this);
    }
}
