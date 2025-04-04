package com.mumu.order.entity;

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
 * @since 2023-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FoodOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "food_order_id", type = IdType.AUTO)
    private Integer foodOrderId;

    private Integer foodId;

    private Integer foodAmount;


    private Integer orderId;

    private Boolean orderValid;


}
