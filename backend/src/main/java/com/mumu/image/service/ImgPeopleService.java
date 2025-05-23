package com.mumu.image.service;

import com.mumu.image.entity.ImgPeople;
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
public interface ImgPeopleService extends IService<ImgPeople> {

    @Transactional
    boolean addImgPeople(int userId, int imgId, List<Integer> tagIds);

    @Transactional
    boolean addImgPeople(int userId, int imgId, String peopleName);

    boolean deleteImgPeople(int userId, int imgId, String peopleName);
}
