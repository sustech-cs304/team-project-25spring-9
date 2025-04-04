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
 * @since 2023-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Food implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "food_id", type = IdType.AUTO)
    private Integer foodId;

    private String foodName;

    private String foodImg;

    /**
     * the amount of food that can be ordered daily
     */
    private Integer foodAmount;

    /**
     * the amount of food that have be ordered today
     */
    private Integer foodOrderedAmount;

    private Integer windowId;
    /**
     * the amount of food that have be ordered up to now
     */
    private Integer foodTotalOrderedAmount;

    private Double foodPrice;

    private Boolean foodValid;
}
