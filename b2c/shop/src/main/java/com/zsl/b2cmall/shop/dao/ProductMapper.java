package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.ProductPO;
import com.zsl.b2cmall.shop.request.ProductQueryVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO tb_product (shop_id, name, category, price, stock, type, description, status, created_at, " +
            "market_price, sale_price, sales, fake_sales, warehouse_stock, store_stock, delivery_types, sort, " +
            "product_type, stock_threshold, cover_image) " +
            "VALUES (#{shopId}, #{name}, #{category}, #{price}, #{stock}, #{type}, #{description}, #{status}, datetime('now','localtime'), " +
            "#{marketPrice}, #{salePrice}, #{sales}, #{fakeSales}, #{warehouseStock}, #{storeStock}, #{deliveryTypes}, #{sort}, " +
            "#{productType}, #{stockThreshold}, #{coverImage})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPO product);

    @Update("UPDATE tb_product SET name=#{name}, category=#{category}, price=#{price}, stock=#{stock}, type=#{type}, " +
            "description=#{description}, market_price=#{marketPrice}, sale_price=#{salePrice}, sales=#{sales}, " +
            "fake_sales=#{fakeSales}, warehouse_stock=#{warehouseStock}, store_stock=#{storeStock}, " +
            "delivery_types=#{deliveryTypes}, sort=#{sort}, product_type=#{productType}, " +
            "stock_threshold=#{stockThreshold}, cover_image=#{coverImage} WHERE id=#{id}")
    int update(ProductPO product);

    @Update("UPDATE tb_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Update("UPDATE tb_product SET status = #{status} WHERE id = #{id} AND shop_id = #{shopId}")
    int updateStatusByShop(@Param("id") Integer id, @Param("shopId") Integer shopId, @Param("status") Integer status);

    @Delete("DELETE FROM tb_product WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM tb_product WHERE id = #{id}")
    ProductPO findById(@Param("id") Integer id);

    @Select("SELECT * FROM tb_product WHERE id = #{id} AND shop_id = #{shopId}")
    ProductPO findByIdAndShop(@Param("id") Integer id, @Param("shopId") Integer shopId);

    @Select("SELECT * FROM tb_product WHERE shop_id = #{shopId} ORDER BY id DESC")
    List<ProductPO> findByShopId(@Param("shopId") Integer shopId);

    /** 分页查询（动态 SQL 见 ProductMapper.xml） */
    List<ProductPO> queryPage(ProductQueryVO query);

    long countQuery(ProductQueryVO query);

    List<ProductPO> queryAll(ProductQueryVO query);

    /** C 端在售商品：分类筛选 + 排序 */
    List<ProductPO> mallList(@Param("category") String category, @Param("sort") String sort);

    /** C 端分类去重 */
    List<String> distinctCategories();

    /** 批量更新（动态字段，列名由 Service 白名单校验） */
    int batchUpdate(@Param("ids") List<Integer> ids, @Param("column") String column, @Param("value") Object value);
}
