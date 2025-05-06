package com.mumu.image.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 
 * </p>
 *
 * @author mumu
 * @since 2025-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer userId;
    @TableId(value = "album_id", type = IdType.AUTO)
    private Integer albumId;
    //    @TableField(value = "album_name",jdbcType = JdbcType.CLOB)
    private String albumName;
//    private String name;
    private String albumDescribtion;


    public Album(Integer userId,Integer albumId, String albumName, String albumDescribtion) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumDescribtion = albumDescribtion;
        this.userId = userId;
    }
    public Album(Integer userId, String albumName, String albumDescribtion) {
        this.albumName = albumName;
        this.albumDescribtion = albumDescribtion;
        this.userId = userId;
    }
}
