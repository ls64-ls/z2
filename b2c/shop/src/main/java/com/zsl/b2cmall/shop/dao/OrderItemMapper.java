package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.OrderItemPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("INSERT INTO tb_order_item (order_id, product_id, product_name, product_image, unit_price, quantity, is_gift) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productImage}, #{unitPrice}, #{quantity}, #{isGift})")
    int insert(OrderItemPO item);

    List<OrderItemPO> findByOrderIds(@Param("orderIds") List<Integer> orderIds);
}
