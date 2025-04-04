package com.mumu.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mumu.store.entity.StoreOrderItem;
import com.mumu.store.mapper.StoreOrderItemMapper;
import com.mumu.store.service.StoreOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
@Service
public class StoreOrderItemServiceImpl extends ServiceImpl<StoreOrderItemMapper, StoreOrderItem> implements StoreOrderItemService {
    @Autowired
    StoreOrderItemMapper mapper;
}
