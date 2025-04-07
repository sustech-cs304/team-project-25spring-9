package com.mumu.image.DTO;

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
    @JsonProperty("timestamp")

    private Date timestamp;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("address")
    private String address;
    @JsonProperty("Camera/Device")
    private String cameraDevice;
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("AutoTags")
    private List<String> autoTags;
    @JsonProperty("personLabel")
    private String personLabel;
}
