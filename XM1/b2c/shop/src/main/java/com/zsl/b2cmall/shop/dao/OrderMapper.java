package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.OrderPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM tb_order WHERE shop_id = #{shopId} ORDER BY id DESC")
    List<OrderPO> findByShopId(@Param("shopId") Integer shopId);

    @Select("SELECT COUNT(*) FROM tb_order WHERE shop_id = #{shopId}")
    int countByShopId(@Param("shopId") Integer shopId);

    @Select("SELECT COUNT(*) FROM tb_order WHERE shop_id = #{shopId} AND status = #{status}")
    int countByShopIdAndStatus(@Param("shopId") Integer shopId, @Param("status") Integer status);

    @Select("SELECT IFNULL(SUM(amount * quantity), 0) FROM tb_order WHERE shop_id = #{shopId} AND status IN (1, 2, 3)")
    Double sumSalesByShopId(@Param("shopId") Integer shopId);
}
