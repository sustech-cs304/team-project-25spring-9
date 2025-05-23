package com.mumu.image.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.image.entity.ImgPeople;
import com.mumu.image.entity.People;
import com.mumu.image.mapper.ImgPeopleMapper;
import com.mumu.image.mapper.PeopleMapper;
import com.mumu.image.service.PeopleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class PeopleServiceImpl extends ServiceImpl<PeopleMapper, People> implements PeopleService {
    @Autowired
    PeopleMapper mapper;
    @Autowired
    ImgPeopleMapper imgPeopleMapper;
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Override
    public boolean isPeopleExists(String people) {
        Integer count = mapper.selectCount(new MPJLambdaWrapper<People>()
                .eq(People::getName, people));
        return count != null && count > 0;
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public List<String> checkAndInsertPeople(int userId, List<String> names) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询数据库中已存在的 (name, userId) 组合
        List<People> existingPeople = mapper.selectList(new LambdaQueryWrapper<People>()
                .select(People::getName) // 仅查询 Name 列
                .eq(People::getUserId, userId) // userId 过滤
                .in(People::getName, names)); // 过滤 Name

        // 提取已存在的 name 集合
        Set<String> existingNames = existingPeople.stream()
                .map(People::getName)
                .collect(Collectors.toSet());

        // 找出需要插入的 (name, userId) 记录
        List<People> newPeople = names.stream()
                .filter(name -> !existingNames.contains(name)) // 过滤掉已存在的
                .map(name -> {
                    People person = new People();
                    person.setName(name);
                    person.setUserId(userId);
                    return person;
                })
                .collect(Collectors.toList());

        // 批量插入新数据
        if (!newPeople.isEmpty()) {
            this.saveBatch(newPeople); // MyBatis-Plus 提供的批量插入
        }
        return newPeople.stream().map(People::getName).collect(Collectors.toList());
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public List<String> deletePeopleAndImgPeople(int userId, List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }

        // 1️⃣ 查询 `people` 表中匹配的 `people_id`
        List<People> existingPeople = mapper.selectList(new LambdaQueryWrapper<People>()
                .select(People::getPeopleId, People::getName)
                .eq(People::getUserId, userId)
                .in(People::getName, names));

        if (existingPeople.isEmpty()) {
            return List.of(); // 没有符合条件的数据
        }

        // 2️⃣ 提取 `people_id` 和 `name`
        Set<Integer> peopleIds = existingPeople.stream().map(People::getPeopleId).collect(Collectors.toSet());
        Set<String> existingNames = existingPeople.stream().map(People::getName).collect(Collectors.toSet());

        // 3️⃣ 先删除 `img_people` 表中 `people_id`
        imgPeopleMapper.delete(new LambdaUpdateWrapper<ImgPeople>()
                .eq(ImgPeople::getUserId, userId)
                .in(ImgPeople::getPeopleId, peopleIds));

        // 4️⃣ 再删除 `people` 表中的记录
       mapper.delete(new LambdaUpdateWrapper<People>()
                .eq(People::getUserId, userId)
                .in(People::getPeopleId, peopleIds));

        return List.copyOf(existingNames); // 返回删除的名字
    }
    /**
     * 根据 tagName 列表查询对应的 tagId 列表
     * @param PeopleNames 需要查询的标签名列表
     * @return 对应的 tagId 列表
     */
    @Override
    public List<Integer> getPeopleIdsByNames(List<String> PeopleNames, int userId) {
        if (PeopleNames == null || PeopleNames.isEmpty()) {
            return List.of(); // 如果输入为空，直接返回空列表
        }

        return mapper.selectList(
                Wrappers.<People>lambdaQuery().in(People::getName, PeopleNames).eq(People::getUserId, userId)
        ).stream().map(People::getPeopleId).collect(Collectors.toList());
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Override
    public Integer getPeopleIdByName(String tagName) {
        People people = mapper.selectOne(
                Wrappers.<People>lambdaQuery().eq(People::getName, tagName)
        );
        return people != null ? people.getPeopleId() : null; // 若未找到，返回 null
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Transactional
    @Override
    public List<String> deleteExistingPeople(int userId, List<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }

        // 1️⃣ 查询数据库中存在的 name
        List<People> existingPeople = mapper.selectList(new LambdaQueryWrapper<People>()
                .select(People::getName)
                .eq(People::getUserId, userId)
                .in(People::getName, names));

        // 2️⃣ 提取已存在的 name
        Set<String> existingNames = existingPeople.stream()
                .map(People::getName)
                .collect(Collectors.toSet());

        if (existingNames.isEmpty()) {
            return List.of(); // 没有需要删除的
        }

        // 3️⃣ 批量删除数据库中存在的 name
        mapper.delete(new LambdaUpdateWrapper<People>()
                .eq(People::getUserId, userId)
                .in(People::getName, existingNames));

        return List.copyOf(existingNames); // 返回被删除的 name
    }
    /**
     * AI-generated-content
     * tool: ChatGPT
     * version: 4
     * usage: 用于生成初始模板，并根据我们项目需求做了小修改
     */
    @Override
    public List<People> getAllPeople(int userId){
        return mapper.selectList(new LambdaQueryWrapper<People>()
                .eq(People::getUserId, userId));
    }
}
