package com.mumu.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.bus.dto.BusDTO;
import com.mumu.bus.entity.Bus;
import com.mumu.bus.mapper.BusMapper;
import com.mumu.bus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
@Service
public class BusServiceImpl extends ServiceImpl<BusMapper, Bus> implements BusService {
    @Autowired
    BusMapper mapper;

    @Override
    public Integer getBusId(String busName) {
        return mapper.selectOne(new LambdaQueryWrapper<Bus>()
                .eq(Bus::getBusName, busName)).getBusId();
    }

    @Override
    public List<BusDTO> getAllBus() {
        return mapper.selectJoinList(BusDTO.class, new MPJLambdaWrapper<Bus>()
                .selectAll(Bus.class)
                .eq(Bus::getBusValid, true)
        );
    }

    @Override
    public Boolean deleteBus(Integer busId) {
        return mapper.update(null, new LambdaUpdateWrapper<Bus>()
                .eq(Bus::getBusId, busId)
                .set(Bus::getBusValid, false)) > 0;
    }

    @Override
    public List<BusDTO> getDeletedBus() {
        return mapper.selectJoinList(BusDTO.class, new MPJLambdaWrapper<Bus>()
                .selectAll(Bus.class)
                .eq(Bus::getBusValid, false)
        );
    }

    @Override
    public Boolean recoverBus(Integer busId) {
        return mapper.update(null, new LambdaUpdateWrapper<Bus>()
                .eq(Bus::getBusId, busId)
                .set(Bus::getBusValid, true)) > 0;
    }
}
