package com.mumu.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.store.entity.DTO.StoreWindowDTO;
import com.mumu.store.entity.StoreWindow;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
public interface StoreWindowService extends IService<StoreWindow> {

    List<StoreWindowDTO> getAllItem();

    List<StoreWindow> getAllWindow();

    Boolean deleteWindow(Integer windowId);
}
