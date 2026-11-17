package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.RefundPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RefundMapper {

    @Select("SELECT COUNT(*) FROM tb_refund WHERE status = '退款申请中'")
    int countPending();

    @Select("SELECT IFNULL(SUM(amount),0) FROM tb_refund WHERE status = '退款完成' " +
            "AND DATE(handled_at) = DATE_ADD(CURDATE(), INTERVAL #{dayOffset} DAY)")
    Double sumCompletedAmount(@Param("dayOffset") int dayOffset);

    @Select("SELECT * FROM tb_refund WHERE order_id = #{orderId} ORDER BY id DESC LIMIT 1")
    RefundPO findByOrderId(@Param("orderId") Integer orderId);
}
