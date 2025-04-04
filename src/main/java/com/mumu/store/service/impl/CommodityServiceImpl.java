package com.mumu.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.store.entity.Commodity;
import com.mumu.store.mapper.CommodityMapper;
import com.mumu.store.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements CommodityService {
    @Autowired
    CommodityMapper mapper;

    @Override
    public Boolean deleteCommodity(Integer commodityId) {
        return mapper.deleteById(commodityId) > 0;

    }

    @Override
    public List<Commodity> getAll() {
        return mapper.selectJoinList(Commodity.class, new MPJLambdaWrapper<Commodity>().orderByAsc(Commodity::getCommodityId));
    }
}
