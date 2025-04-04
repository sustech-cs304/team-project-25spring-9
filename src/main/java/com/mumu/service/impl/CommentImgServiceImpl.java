package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.Comment;
import com.mumu.entity.CommentImg;
import com.mumu.mapper.CommentImgMapper;
import com.mumu.service.CommentImgService;
import com.mumu.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@Service
public class CommentImgServiceImpl extends ServiceImpl<CommentImgMapper, CommentImg> implements CommentImgService {
    @Autowired
    CommentImgMapper mapper;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.commentPath}")
    private String commentImgPath;

    @Override
    // 获取评论最大的图片的名字
    public String firstImgIdGroupByCommentId(int commentId) {
        MPJLambdaWrapper<CommentImg> queryWrapper = new MPJLambdaWrapper<CommentImg>()
                .select(CommentImg::getImgName)
                .eq(CommentImg::getCommentId, commentId)
                .orderByAsc("commentImg_id")
                .last("limit 1");
        return mapper.selectJoinOne(String.class, queryWrapper);
    }

    @Override
    // 获取评论最大的图片的名字
    public String largestImgIdGroupByCommentId(int commentId) {
        MPJLambdaWrapper<CommentImg> queryWrapper = new MPJLambdaWrapper<CommentImg>()
                .select(CommentImg::getImgName)
                .eq(CommentImg::getCommentId, commentId)
                .orderByDesc("commentImg_id")
                .last("limit 1");
        return mapper.selectJoinOne(String.class, queryWrapper);
    }

    @Override
    // 通过id获取评论的所有图片
    public List<String> allCommentImg(int commentId) {
        MPJLambdaWrapper<CommentImg> queryWrapper = new MPJLambdaWrapper<CommentImg>()
                .select(CommentImg::getImgName)
                .eq(CommentImg::getCommentId, commentId);
        return mapper.selectJoinList(String.class, queryWrapper);
    }

    @Override
    // 删除一张评论图片
    public String deleteOneImg(int commentId, String imgName) {
        String result = minioUtilS.removeFile(commentImgPath, imgName);
        if (!result.equals("删除文件成功")) {
            return "删除文件失败";
        }
        mapper.deleteJoin(JoinWrappers.delete(CommentImg.class)
                .delete(CommentImg.class)
                .eq(Comment::getCommentId, commentId)
                .eq(CommentImg::getImgName, imgName));
        return "删除文件成功";
    }

    @Override
    // 删除所有评论图片
    public String deleteImgByCommentId(int commentId) {
        List<String> imgNameList = allCommentImg(commentId);
        for (String imgName : imgNameList) {
            String result = deleteOneImg(commentId, imgName);
            if (!result.equals("删除文件成功")) {
                return "删除文件失败";
            }
        }
        return "删除文件成功";
    }
}
