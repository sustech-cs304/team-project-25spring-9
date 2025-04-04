package com.mumu.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.store.entity.Commodity;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
public interface CommodityService extends IService<Commodity> {

    Boolean deleteCommodity(Integer commodityId);

    List<Commodity> getAll();
}
