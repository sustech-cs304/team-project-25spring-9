package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GlobalDTO {
    private List<Integer> buildingId;
    private List<Integer> commentId;
    private List<Integer> replyId;

    public GlobalDTO(List<Integer> buildingId, List<Integer> commentId, List<Integer> replyId) {
        this.buildingId = buildingId;
        this.commentId = commentId;
        this.replyId = replyId;
    }
}
