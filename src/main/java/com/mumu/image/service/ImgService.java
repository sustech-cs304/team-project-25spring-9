package com.mumu.image.service;

import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.Img;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2025-03-14
 */
public interface ImgService extends IService<Img> {
    List<ImgDTO> getAllImgInfo(Img img);

    List<ImgDTO> getImagesByTags(ImgDTO img, int offset, int limit);

    // 通过建筑id获取建筑信息
}
