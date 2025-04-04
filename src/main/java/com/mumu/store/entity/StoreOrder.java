package com.mumu.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StoreOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    private Integer userId;
    private Double price;

    private LocalDateTime orderTime;

    private LocalDateTime payTime;

    private Boolean payed;


}
