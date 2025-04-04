package com.mumu.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Data
@TableName("orders")
@EqualsAndHashCode(callSuper = false)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    private Integer userId;
    private LocalDateTime foodOrderTime;
    private LocalDateTime payTime;
    private LocalDateTime foodGetTime;
    private Boolean payed;
    private Boolean ordersValid;
    private Boolean orderTaken;
    private Double orderPrice;

}
