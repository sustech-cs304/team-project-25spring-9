package com.mumu.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.store.entity.Commodity;
import com.mumu.store.entity.DTO.StoreWindowDTO;
import com.mumu.store.entity.StoreWindow;
import com.mumu.store.mapper.StoreWindowMapper;
import com.mumu.store.service.StoreWindowService;
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
public class StoreWindowServiceImpl extends ServiceImpl<StoreWindowMapper, StoreWindow> implements StoreWindowService {
    @Autowired
    StoreWindowMapper mapper;

    @Override
    public List<StoreWindowDTO> getAllItem() {
        MPJLambdaWrapper<StoreWindow> lambdaWrapper = new MPJLambdaWrapper<StoreWindow>()
                .selectAll(StoreWindow.class)
                .selectCollection(Commodity.class, StoreWindowDTO::getCommodityList)
                .leftJoin(Commodity.class, Commodity::getWindowId, StoreWindow::getWindowId);
        return mapper.selectJoinList(StoreWindowDTO.class, lambdaWrapper);
    }

    @Override
    public List<StoreWindow> getAllWindow() {
        MPJLambdaWrapper<StoreWindow> lambdaWrapper = new MPJLambdaWrapper<StoreWindow>()
                .selectAll(StoreWindow.class);
        return mapper.selectJoinList(StoreWindow.class, lambdaWrapper);
    }

    @Override
    public Boolean deleteWindow(Integer windowId) {
        return mapper.deleteById(windowId) > 0;

    }
}
