package com.zsl.b2cmall.employee.service.event.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.zsl.b2cmall.employee.dao.EmployeeMapper;
import com.zsl.b2cmall.employee.entity.EmployeePO;
import com.zsl.b2cmall.employee.service.event.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 用户状态观察者：更新用户表的最后登录时间、累计登录次数
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserStatusObserver implements InitializingBean {

    private final EventBus eventBus;
    private final EmployeeMapper employeeMapper;

    @Subscribe
    public void onLogin(LoginEvent event) {
        log.info("[用户状态] 更新登录状态: employeeId={}", event.getEmployeeId());

        EmployeePO employee = employeeMapper.getById(event.getEmployeeId());
        if (employee == null) {
            log.warn("[用户状态] 员工不存在, employeeId={}", event.getEmployeeId());
            return;
        }

        int newLoginCount = (employee.getLoginCount() == null ? 0 : employee.getLoginCount()) + 1;
        employeeMapper.updateLoginInfo(event.getEmployeeId(), newLoginCount);
        log.info("[用户状态] 登录次数已更新为: {}", newLoginCount);
    }

    @Override
    public void afterPropertiesSet() {
        eventBus.register(this);
    }
}
