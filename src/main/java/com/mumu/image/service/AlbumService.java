package com.mumu.image.service;

import com.mumu.image.entity.Album;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2025-05-06
 */
public interface AlbumService extends IService<Album> {

    List<Album> getlist(Integer userId);
}
