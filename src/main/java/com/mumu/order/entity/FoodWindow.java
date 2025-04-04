package com.mumu.order.entity;

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
 * @since 2023-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FoodWindow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "window_id", type = IdType.AUTO)
    private Integer windowId;

    private Integer restaurantId;

    private String windowName;

    private Boolean windowValid;


}
