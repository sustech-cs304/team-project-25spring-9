package com.mumu.image.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mumu.image.entity.ImgPeople;
import com.mumu.image.entity.People;
import com.mumu.image.mapper.ImgPeopleMapper;
import com.mumu.image.mapper.PeopleMapper;
import com.mumu.image.service.ImgPeopleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mumu.image.service.PeopleService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
public class ImgPeopleServiceImpl extends ServiceImpl<ImgPeopleMapper, ImgPeople> implements ImgPeopleService {
    @Autowired
    ImgPeopleMapper mapper;
    @Autowired
    PeopleMapper peopleMapper;
    @Autowired
    PeopleService peopleService;
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public boolean addImgPeople(int userId, int imgId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return false; // 没有需要插入的数据
        }

        // 1️⃣ 查询数据库中已存在的记录
        List<ImgPeople> existingTags = mapper.selectList(
                Wrappers.<ImgPeople>lambdaQuery()
                        .eq(ImgPeople::getUserId, userId)
                        .eq(ImgPeople::getImgId, imgId)
                        .in(ImgPeople::getPeopleId, tagIds)
        );

        // 2️⃣ 提取已存在的 tagId
        Set<Integer> existingTagIds = existingTags.stream()
                .map(ImgPeople::getPeopleId)
                .collect(Collectors.toSet());

        // 3️⃣ 过滤掉已存在的 tagId，创建新的 `ImgTag` 记录
        List<ImgPeople> newImgTags = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId)) // 只插入不存在的
                .map(tagId -> new ImgPeople(userId, imgId, tagId)) // 创建对象
                .collect(Collectors.toList());

        // 4️⃣ 批量插入新数据
        if (!newImgTags.isEmpty()) {
            return this.saveBatch(newImgTags);
        }
        return false; // 没有新数据需要插入
    }
    @Transactional
    @Override
    public boolean addImgPeople(int userId, int imgId, String peopleName) {
        List<String> al=new ArrayList<>();
        al.add(peopleName);
        peopleService.checkAndInsertPeople(userId,al);
        return addImgPeople(userId, imgId, new ArrayList<>(Collections.singletonList(
                peopleMapper.selectOne(new LambdaQueryWrapper<People>()
                                .select(People::getPeopleId)
                                .eq(People::getUserId, userId)
                                .eq(People::getName, peopleName))
                        .getPeopleId()
        )));
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Override
    public boolean deleteImgPeople(int userId, int imgId, String peopleName) {
        int peopleId=peopleMapper.selectOne(new LambdaQueryWrapper<People>()
                        .select(People::getPeopleId)
                        .eq(People::getUserId, userId)
                        .eq(People::getName, peopleName))
                .getPeopleId();
        return mapper.delete(new LambdaUpdateWrapper<ImgPeople>()
                .eq(ImgPeople::getUserId, userId)
                .eq(ImgPeople::getPeopleId, peopleId)
                .eq(ImgPeople::getImgId,imgId))>0;
    }
}
