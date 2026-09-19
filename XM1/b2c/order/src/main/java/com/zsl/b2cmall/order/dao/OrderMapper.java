package com.zsl.b2cmall.order.dao;

import com.zsl.b2cmall.order.entity.OrderPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM tb_order WHERE id = #{id}")
    OrderPO findById(@Param("id") Integer id);

    @Select("SELECT * FROM tb_order WHERE shop_id = #{shopId} ORDER BY id DESC")
    List<OrderPO> findByShopId(@Param("shopId") Integer shopId);

    @Insert("INSERT INTO tb_order (shop_id, product_id, product_name, amount, quantity, status, buyer_name, buyer_phone, buyer_address) " +
            "VALUES (#{shopId}, #{productId}, #{productName}, #{amount}, #{quantity}, #{status}, #{buyerName}, #{buyerPhone}, #{buyerAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(OrderPO orderPO);

    @Update("UPDATE tb_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
}
