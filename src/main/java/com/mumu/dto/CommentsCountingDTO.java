package com.mumu.dto;

import com.mumu.entity.Comment;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CommentsCountingDTO {
    private Long count;

    private List<Comment> comments;

    public CommentsCountingDTO(Long count, List<Comment> comments) {
        this.count = count;
        this.comments = comments;
    }
}
