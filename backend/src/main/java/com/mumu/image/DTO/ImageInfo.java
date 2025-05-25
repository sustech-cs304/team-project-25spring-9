package com.mumu.image.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageInfo {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("Timestamp")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date timestamp;
    @JsonProperty("Latitude")
    private Double latitude;
    @JsonProperty("Longitude")
    private Double longitude;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("Camera/Device")
    private String cameraDevice;
    @JsonProperty("Caption")
    private String caption;
    @JsonProperty("AutoTags")
    private List<String> autoTags;
    @JsonProperty("PersonLabel")
    private List<String> personLabel;
}
