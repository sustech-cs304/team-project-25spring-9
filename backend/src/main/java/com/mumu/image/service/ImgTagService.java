package com.mumu.image.service;

import com.mumu.image.entity.ImgTag;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
public interface ImgTagService extends IService<ImgTag> {

    @Transactional
    boolean addImgTag(int userId, int imgId, List<Integer> tagIds);

    boolean addImgTag(int userId, int imgId, String tagName);

    boolean deleteImgTag(int userId, int imgId, String tagName);
}
