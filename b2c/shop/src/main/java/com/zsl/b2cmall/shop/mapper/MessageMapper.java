package com.zsl.b2cmall.shop.mapper;

import com.zsl.b2cmall.shop.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO tb_message (shop_id, sender_id, title, content, msg_type, is_read, order_id, order_no, created_at, updated_at) " +
            "VALUES (#{shopId}, #{senderId}, #{title}, #{content}, #{msgType}, #{isRead}, #{orderId}, #{orderNo}, #{createdAt}, #{updatedAt})")
    int insert(Message message);

    @Select("SELECT * FROM tb_message WHERE shop_id = #{shopId} ORDER BY created_at DESC, id DESC")
    List<Message> findByShopId(@Param("shopId") Integer shopId);

    @Select("SELECT COUNT(*) FROM tb_message WHERE shop_id = #{shopId} AND is_read = 0")
    int countUnread(@Param("shopId") Integer shopId);

    @Update("UPDATE tb_message SET is_read = 1, read_time = NOW() WHERE id = #{id}")
    int markRead(@Param("id") Integer id);
}
