package com.mumu.image.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mumu.image.DTO.ImgDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mumu
 * @since 2025-03-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Img implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "img_id", type = IdType.AUTO)
    private Integer imgId;

    private String imgName;

    private Date imgDate;

    private Boolean pub;

    private Boolean valid;

    private String imgPos;

    private Integer userId;

    public Img(ImgDTO img) {
        this.imgName=img.getImgName();
        this.imgDate=img.getImgDate();
        this.pub=img.getPub();
        this.imgPos=img.getImgPos();
        this.userId=img.getUserId();
    }
}
