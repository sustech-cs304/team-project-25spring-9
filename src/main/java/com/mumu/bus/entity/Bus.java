package com.mumu.bus.entity;

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
 * @since 2023-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Bus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "bus_id", type = IdType.AUTO)
    private Integer busId;
    private String busName;
    private Boolean busValid;
}
