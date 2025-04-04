package com.mumu.image.service;

import com.mumu.image.entity.Tag;
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
public interface TagService extends IService<Tag> {

    boolean isTagExists(String tag);

    @Transactional
    boolean checkAndInsertTag(int userId, List<String> tags);

    List<Integer> getTagIdsByNames(List<String> tagNames,int tagId);

    Integer getTagIdByName(String tagName);

    @Transactional
    List<String> deleteExistingTag(int userId, List<String> names);

    List<Tag> getAllTag(int userId);
}
