package com.zsl.b2cmall.shop.mapper;

import com.zsl.b2cmall.shop.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO tb_messages (shop_id, sender_id, title, content, msg_type, is_read, created_at, updated_at) " +
            "VALUES (#{shopId}, #{senderId}, #{title}, #{content}, #{msgType}, #{isRead}, #{createdAt}, #{updatedAt})")
    int insert(Message message);

    @Select("SELECT * FROM tb_messages WHERE shop_id = #{shopId} ORDER BY id DESC")
    List<Message> findByShopId(@Param("shopId") Integer shopId);
}
