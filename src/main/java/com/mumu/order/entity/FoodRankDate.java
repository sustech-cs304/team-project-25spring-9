package com.mumu.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

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
public class FoodRankDate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "food_rank_id", type = IdType.AUTO)
    private Integer foodRankId;

    private Integer foodId;

    private Integer foodCnt;

    private LocalDate date;
}
