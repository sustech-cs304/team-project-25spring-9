package com.mumu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "building_id", type = IdType.AUTO)
    private Integer commentImgId;

    private Integer commentId;

    private String imgName;
}
