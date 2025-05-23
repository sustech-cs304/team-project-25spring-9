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
public class ImgPeople implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "ip_id", type = IdType.AUTO)
    private Integer ipId;
    private Integer userId;

    private Integer imgId;

    private Integer peopleId;

    public ImgPeople(int userId,int  imgId,int peopleId) {
        this.userId = userId;
        this.imgId = imgId;
        this.peopleId = peopleId;
    }

}
