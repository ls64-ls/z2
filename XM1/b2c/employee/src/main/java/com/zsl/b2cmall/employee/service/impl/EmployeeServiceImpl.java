package com.zsl.b2cmall.employee.service.impl;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.zsl.b2cmall.common.utils.JWTUtil;
import com.zsl.b2cmall.employee.dao.EmployeeMapper;
import com.zsl.b2cmall.employee.entity.EmployeePO;
import com.zsl.b2cmall.employee.request.AddEmployeeRequestVO;
import com.zsl.b2cmall.employee.request.LoginRequestVO;
import com.zsl.b2cmall.employee.service.EmployeeService;
import com.zsl.b2cmall.employee.service.event.LoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EventBus eventBus;
    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    private static final String DEFAULT_AVATAR = "http://47.100.22.158:8848/nacos/img/logo-2000-390.svg";
    private static final String TOKEN_PREFIX = "employee:token:";
    private static final long TOKEN_EXPIRE_HOURS = 24;

    @Override
    public Integer addEmployee(AddEmployeeRequestVO vo) {
        Assert.notNull(vo.getUsername(), "用户名不能为空");
        Assert.notNull(vo.getShopId(), "店铺ID不能为空");
        Assert.notNull(vo.getPassword(), "密码不能为空");

        EmployeePO po = new EmployeePO();
        BeanUtils.copyProperties(vo, po);
        po.setAvatarUrl(DEFAULT_AVATAR);
        po.setStatus(1);
        po.setLoginCount(0);
        Date now = new Date();
        po.setCreatedAt(now);
        po.setUpdatedAt(now);

        Integer row = employeeMapper.save(po);
        Assert.equals(row, 1, "保存员工信息失败");
        return po.getId();
    }

    @Override
    public String login(LoginRequestVO req) {
        EmployeePO employeePO = employeeMapper.getEmployee(req.getShopId(), req.getUsername());
        if (Objects.isNull(employeePO)) {
            throw new RuntimeException("账户不存在");
        }
        if (!Objects.equals(employeePO.getPassword(), req.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成 JWT Token
        Map<String, Object> payload = Maps.newHashMapWithExpectedSize(3);
        payload.put("id", employeePO.getId());
        payload.put("username", employeePO.getUsername());
        payload.put("shopId", employeePO.getShopId());
        String token = JWTUtil.createToken(payload);

        // 存入 Redis
        String redisKey = TOKEN_PREFIX + employeePO.getId();
        redisTemplate.opsForValue().set(redisKey, token, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        // 发布登录事件（观察者模式：审计日志、用户状态更新）
        LoginEvent loginEvent = LoginEvent.builder()
                .employeeId(employeePO.getId())
                .shopId(employeePO.getShopId())
                .username(employeePO.getUsername())
                .loginIp(getClientIp())
                .loginDevice(request.getHeader("User-Agent"))
                .loginLocation("未知")
                .loginTime(new Date())
                .build();
        eventBus.post(loginEvent);

        log.info("员工登录成功: id={}, username={}", employeePO.getId(), employeePO.getUsername());
        return token;
    }

    @Override
    public boolean checkToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (!JWTUtil.verify(token)) {
            return false;
        }
        Map<String, Object> payload = JWTUtil.getPayload(token);
        if (payload == null || payload.get("id") == null) {
            return false;
        }
        Integer employeeId = Integer.valueOf(payload.get("id").toString());
        String redisKey = TOKEN_PREFIX + employeeId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);
        return token.equals(storedToken);
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
