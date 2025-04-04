package com.mumu.bus.service;

import com.mumu.bus.dto.BusDTO;
import com.mumu.bus.entity.Bus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
public interface BusService extends IService<Bus> {

    Integer getBusId(String busName);

    List<BusDTO> getAllBus();

    Boolean deleteBus(Integer busId);

    List<BusDTO> getDeletedBus();

    Boolean recoverBus(Integer busId);
}
