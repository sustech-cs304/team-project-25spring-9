package com.mumu.store.entity;

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
public class StoreOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer storeOrderItemId;

    private Integer orderId;

    private Integer itemId;

    private Integer itemCount;


}
