package com.mumu.image.entity;

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
 * @since 2025-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class People implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "people_id", type = IdType.AUTO)
    private Integer peopleId;

    private String name;

    private Integer userId;

    private Boolean valid;

    private String nickname;


}
