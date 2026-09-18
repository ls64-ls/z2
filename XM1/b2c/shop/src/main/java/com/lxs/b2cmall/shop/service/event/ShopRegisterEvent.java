package com.lxs.b2cmall.shop.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRegisterEvent implements Serializable {

    private Integer shopId;
    private String adminAccount;
    private String adminPwd;
}
