package com.mumu.image.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ImgTag implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "it_id", type = IdType.AUTO)
    private Integer itId;
    private Integer userId;

    private Integer imgId;

    private Integer tagId;
    public ImgTag(Integer userId,Integer imgId,Integer tagId) {
        this.userId = userId;
        this.imgId = imgId;
        this.tagId = tagId;
    }

}
