package com.mumu.image.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Data
public class ImgDTO {
    private Integer imgId;

    private String imgName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date imgDate;
    private Integer albumId;
    private Boolean pub;
    private String albumName;
    private String imgPos;
    private String imgDescribtion;
    private Integer userId;
    private ResponseEntity<byte[]> img;
    private List<String> peoples;
    private List<String> tags;
    //duplicated
    private String name;
    private String tagName;
}
