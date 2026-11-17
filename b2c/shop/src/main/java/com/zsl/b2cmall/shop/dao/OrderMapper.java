package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.OrderPO;
import com.zsl.b2cmall.shop.request.OrderQueryVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /** B 端订单分页（动态 SQL 见 OrderMapper.xml） */
    List<OrderPO> queryPage(OrderQueryVO query);

    long countQuery(OrderQueryVO query);

    List<OrderPO> queryAll(OrderQueryVO query);

    @Select("SELECT * FROM tb_order WHERE id = #{id}")
    OrderPO findById(@Param("id") Integer id);

    List<OrderPO> findByIds(@Param("ids") List<Integer> ids);

    @Select("SELECT COUNT(*) FROM tb_order WHERE product_id = #{productId} AND deleted = 0")
    int countByProductId(@Param("productId") Integer productId);

    /** 状态卡片计数 */
    @Select("SELECT COUNT(*) FROM tb_order WHERE shop_id = #{shopId} AND status = #{status} AND deleted = 0")
    int countByStatus(@Param("shopId") Integer shopId, @Param("status") String status);

    /** 全部订单计数 */
    @Select("SELECT COUNT(*) FROM tb_order WHERE shop_id = #{shopId} AND deleted = 0")
    int countAll(@Param("shopId") Integer shopId);

    /** 售后单据数 */
    @Select("SELECT COUNT(*) FROM tb_refund WHERE status = '退款申请中'")
    int countRefund();

    /** 库存预警商品数 */
    @Select("SELECT COUNT(*) FROM tb_product WHERE shop_id = #{shopId} AND stock_threshold > 0 " +
            "AND (IFNULL(warehouse_stock,0) + IFNULL(store_stock,0)) < stock_threshold")
    int countStockWarning(@Param("shopId") Integer shopId);

    /** 支付金额（按日期偏移，0=今天，-1=昨天） */
    @Select("SELECT IFNULL(SUM(amount),0) FROM tb_order WHERE shop_id = #{shopId} AND deleted = 0 " +
            "AND status IN ('PAID','SENT','COMPLETED') AND DATE(paid_at) = DATE_ADD(CURDATE(), INTERVAL #{dayOffset} DAY)")
    Double sumPaidAmount(@Param("shopId") Integer shopId, @Param("dayOffset") int dayOffset);

    /** 支付订单数（按日期偏移） */
    @Select("SELECT COUNT(*) FROM tb_order WHERE shop_id = #{shopId} AND deleted = 0 " +
            "AND status IN ('PAID','SENT','COMPLETED') AND DATE(paid_at) = DATE_ADD(CURDATE(), INTERVAL #{dayOffset} DAY)")
    int countPaidOrders(@Param("shopId") Integer shopId, @Param("dayOffset") int dayOffset);

    /** 支付客户数（按日期偏移，独立客户） */
    @Select("SELECT COUNT(DISTINCT IFNULL(user_id, buyer_phone)) FROM tb_order WHERE shop_id = #{shopId} AND deleted = 0 " +
            "AND status IN ('PAID','SENT','COMPLETED') AND DATE(paid_at) = DATE_ADD(CURDATE(), INTERVAL #{dayOffset} DAY)")
    int countPaidCustomers(@Param("shopId") Integer shopId, @Param("dayOffset") int dayOffset);

    /** 按小时统计支付金额 */
    @Select("SELECT HOUR(paid_at) AS h, IFNULL(SUM(amount),0) AS amt FROM tb_order " +
            "WHERE shop_id = #{shopId} AND deleted = 0 AND status IN ('PAID','SENT','COMPLETED') " +
            "AND DATE(paid_at) = DATE_ADD(CURDATE(), INTERVAL #{dayOffset} DAY) GROUP BY HOUR(paid_at)")
    List<Map<String, Object>> sumHourly(@Param("shopId") Integer shopId, @Param("dayOffset") int dayOffset);

    /** 发货 */
    @Update("UPDATE tb_order SET status = 'SENT', logistics_type = #{logisticsType}, delivery_no = #{deliveryNo}, " +
            "shipped_at = NOW() WHERE id = #{id} AND status = 'PAID'")
    int markSent(@Param("id") Integer id, @Param("logisticsType") String logisticsType,
                 @Param("deliveryNo") String deliveryNo);

    /** 确认收货 */
    @Update("UPDATE tb_order SET status = 'COMPLETED', completed_at = NOW() " +
            "WHERE id = #{id} AND status = 'SENT'")
    int markCompleted(@Param("id") Integer id);

    /** 软删除用户订单 */
    @Update("UPDATE tb_order SET deleted = 1 WHERE id = #{id} AND user_id = #{userId}")
    int softDelete(@Param("id") Integer id, @Param("userId") Integer userId);

    /** C 端订单列表 */
    List<OrderPO> customerList(@Param("userId") Integer userId, @Param("status") String status,
                               @Param("orderNo") String orderNo, @Param("offset") int offset,
                               @Param("limit") int limit);

    long customerCount(@Param("userId") Integer userId, @Param("status") String status,
                       @Param("orderNo") String orderNo);

    @Select("SELECT COUNT(*) FROM tb_order WHERE user_id = #{userId} AND deleted = 0 " +
            "AND (#{status} IS NULL OR status = #{status})")
    int customerStatusCount(@Param("userId") Integer userId, @Param("status") String status);
}
