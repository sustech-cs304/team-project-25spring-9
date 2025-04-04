package com.mumu.bus.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BusDTO {
    private Integer busId;
    private String busName;
    private MultipartFile busPath;
}
