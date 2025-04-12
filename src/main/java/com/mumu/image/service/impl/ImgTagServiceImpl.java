package com.mumu.image.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mumu.image.entity.ImgTag;
import com.mumu.image.entity.Tag;
import com.mumu.image.mapper.ImgTagMapper;
import com.mumu.image.mapper.TagMapper;
import com.mumu.image.service.ImgTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@Service
public class ImgTagServiceImpl extends ServiceImpl<ImgTagMapper, ImgTag> implements ImgTagService {
    @Autowired
    ImgTagMapper imgTagMapper;
    @Autowired
    TagMapper tagMapper;
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public boolean addImgTag(int userId, int imgId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return false; // 没有需要插入的数据
        }

        // 1️⃣ 查询数据库中已存在的记录
        List<ImgTag> existingTags = imgTagMapper.selectList(
                Wrappers.<ImgTag>lambdaQuery()
                        .eq(ImgTag::getUserId, userId)
                        .eq(ImgTag::getImgId, imgId)
                        .in(ImgTag::getTagId, tagIds)
        );

        // 2️⃣ 提取已存在的 tagId
        Set<Integer> existingTagIds = existingTags.stream()
                .map(ImgTag::getTagId)
                .collect(Collectors.toSet());

        // 3️⃣ 过滤掉已存在的 tagId，创建新的 `ImgTag` 记录
        List<ImgTag> newImgTags = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId)) // 只插入不存在的
                .map(tagId -> new ImgTag(userId, imgId, tagId)) // 创建对象
                .collect(Collectors.toList());

        // 4️⃣ 批量插入新数据
        if (!newImgTags.isEmpty()) {
            return this.saveBatch(newImgTags);
        }
        return false; // 没有新数据需要插入
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public boolean addImgTag(int userId, int imgId, String tagName) {
        return addImgTag(userId, imgId, new ArrayList<>(Collections.singletonList(
                tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                                .select(Tag::getTagId)
                                .eq(Tag::getUserId, userId)
                                .eq(Tag::getTagName, tagName))
                        .getTagId()
        )));
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Override
    public boolean deleteImgTag(int userId, int imgId, String tagName) {
        int tagId=tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                        .select(Tag::getTagId)
                        .eq(Tag::getUserId, userId)
                        .eq(Tag::getTagName, tagName))
                .getTagId();
        return imgTagMapper.delete(new LambdaUpdateWrapper<ImgTag>()
                .eq(ImgTag::getUserId, userId)
                .eq(ImgTag::getTagId, tagId)
                .eq(ImgTag::getImgId,imgId))>0;
    }
}
