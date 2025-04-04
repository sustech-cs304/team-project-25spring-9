package com.mumu.store.entity.DTO;

import com.mumu.store.entity.Commodity;
import lombok.Data;

import java.util.List;

@Data
public class StoreWindowDTO {
    private Integer windowId;

    private String windowName;

    private List<Commodity> commodityList;

}
