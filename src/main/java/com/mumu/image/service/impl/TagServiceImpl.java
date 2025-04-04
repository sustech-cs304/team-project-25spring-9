package com.mumu.image.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.image.entity.People;
import com.mumu.image.entity.Tag;
import com.mumu.image.mapper.ImgTagMapper;
import com.mumu.image.mapper.TagMapper;
import com.mumu.image.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    TagMapper mapper;
    @Autowired
    ImgTagMapper imgMapper;
    @Override
    public boolean isTagExists(String tag) {
        Integer count = mapper.selectCount(new MPJLambdaWrapper<Tag>()
                .eq(Tag::getTagName, tag));
        return count != null && count > 0;
    }
    @Transactional
    @Override
    public boolean checkAndInsertTag(int userId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }

        // 查询数据库中已存在的 (name, userId) 组合
        List<Tag> existingTag = mapper.selectList(new LambdaQueryWrapper<Tag>()
                .select(Tag::getTagName) // 仅查询 Name 列
                .eq(Tag::getUserId, userId) // userId 过滤
                .in(Tag::getTagName, tags)); // 过滤 Name

        // 提取已存在的 name 集合
        Set<String> existingNames = existingTag.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());

        // 找出需要插入的 (name, userId) 记录
        List<Tag> newTags = tags.stream()
                .filter(tag -> !existingNames.contains(tag)) // 过滤掉已存在的
                .map(tag -> {
                    Tag t = new Tag();
                    t.setTagName(tag);
                    t.setUserId(userId);
                    return t;
                })
                .collect(Collectors.toList());

        // 批量插入新数据
        if (!newTags.isEmpty()) {
            this.saveBatch(newTags); // MyBatis-Plus 提供的批量插入
        }
        return true;
    }

    /**
     * 根据 tagName 列表查询对应的 tagId 列表
     * @param tagNames 需要查询的标签名列表
     * @return 对应的 tagId 列表
     */
    @Override
    public List<Integer> getTagIdsByNames(List<String> tagNames,int userId) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of(); // 如果输入为空，直接返回空列表
        }

        return mapper.selectList(
                Wrappers.<Tag>lambdaQuery().in(Tag::getTagName, tagNames).eq(Tag::getUserId, userId)
        ).stream().map(Tag::getTagId).collect(Collectors.toList());
    }
    @Override
    public Integer getTagIdByName(String tagName) {
        Tag tag = mapper.selectOne(
                Wrappers.<Tag>lambdaQuery().eq(Tag::getTagName, tagName)
        );
        return tag != null ? tag.getTagId() : null; // 若未找到，返回 null
    }
    @Transactional
    @Override
    public List<String> deleteExistingTag(int userId, List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }

        // 1️⃣ 查询数据库中存在的 name
        List<Tag> existingPeople = mapper.selectList(new LambdaQueryWrapper<Tag>()
                .select(Tag::getTagName)
                .eq(Tag::getUserId, userId)
                .in(Tag::getTagName, names));

        // 2️⃣ 提取已存在的 name
        Set<String> existingNames = existingPeople.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());

        if (existingNames.isEmpty()) {
            return List.of(); // 没有需要删除的
        }

        // 3️⃣ 批量删除数据库中存在的 name
        mapper.delete(new LambdaUpdateWrapper<Tag>()
                .eq(Tag::getUserId, userId)
                .in(Tag::getTagName, existingNames));

        return List.copyOf(existingNames); // 返回被删除的 name
    }

    @Override
    public List<Tag> getAllTag(int userId){
        return mapper.selectList(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getUserId, userId));
    }
}
