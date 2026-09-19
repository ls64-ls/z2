package com.zsl.b2cmall.shop.dao;

import com.zsl.b2cmall.shop.entity.ProductPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO tb_product (shop_id, name, category, price, stock, type, description, status) " +
            "VALUES (#{shopId}, #{name}, #{category}, #{price}, #{stock}, #{type}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPO product);

    @Update("UPDATE tb_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Select("SELECT * FROM tb_product WHERE shop_id = #{shopId} ORDER BY id DESC")
    List<ProductPO> findByShopId(@Param("shopId") Integer shopId);

    @Select("SELECT * FROM tb_product WHERE id = #{id}")
    ProductPO findById(@Param("id") Integer id);
}
