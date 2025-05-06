package com.mumu.image.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mumu.image.entity.Album;
import com.mumu.image.mapper.AlbumMapper;
import com.mumu.image.service.AlbumService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mumu
 * @since 2025-05-06
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {
@Autowired
    AlbumMapper albumMapper;
@Override
public List<Album> getlist(Integer userId){
    return albumMapper.selectList( new LambdaQueryWrapper<Album>()
            .eq(Album::getUserId, userId));
}
}
