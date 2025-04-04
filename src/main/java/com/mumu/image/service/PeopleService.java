package com.mumu.image.service;

import com.mumu.image.entity.People;
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
public interface PeopleService extends IService<People> {

    boolean isPeopleExists(String people);

    @Transactional
    List<String> checkAndInsertPeople(int userId, List<String> names);

    @Transactional
    List<String> deletePeopleAndImgPeople(int userId, List<String> names);

    List<Integer> getPeopleIdsByNames(List<String> PeopleNames, int userId);

    Integer getPeopleIdByName(String tagName);

    @Transactional
    List<String> deleteExistingPeople(int userId, List<String> names);

    List<People> getAllPeople(int userId);
}
