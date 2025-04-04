package com.mumu.store.entity;

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
 * @since 2023-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Commodity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String commodityName;

    @TableId(value = "commodity_id", type = IdType.AUTO)
    private Integer commodityId;
    private Integer amountLeft;
    private Double price;
    private Integer windowId;

}
