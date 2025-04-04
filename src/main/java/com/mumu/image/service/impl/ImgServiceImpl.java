package com.mumu.image.service.impl;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.entity.ImgPeople;
import com.mumu.image.entity.ImgTag;
import com.mumu.image.entity.Tag;
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.service.ImgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mumu.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2025-03-14
 */
@Service
public class ImgServiceImpl extends ServiceImpl<ImgMapper, Img> implements ImgService {
    @Autowired
    ImgMapper mapper;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.uploadImage}")
    private String ImgPath;
    @Override
    public List<ImgDTO> getAllImgInfo(Img img) {
        return mapper.selectJoinList(ImgDTO.class, new MPJLambdaWrapper<Img>()
                .selectAll(Img.class)
                .eq(img.getUserId() != null, Img::getImgId, img.getImgId())
                .eq(img.getImgId() != null, Img::getImgId, img.getImgId())
                .eq(img.getImgPos() != null, Img::getImgPos, img.getImgPos())
                .eq(img.getImgDate() != null, Img::getImgDate, img.getImgDate())
                .eq(img.getImgName() != null, Img::getImgName, img.getImgName())
                .eq(img.getPub() != null, Img::getPub, img.getPub())
        );
    }

    @Override
    public List<ImgDTO> getImagesByTags(ImgDTO img, int offset, int limit) {
        if (img.getTags() == null) {
            img.setTags(new ArrayList<String>());
        }
        if (img.getPeoples() == null) {
            img.setPeoples(new ArrayList<String>());
        }
        // 1️⃣ 先查询符合条件的图片ID
        List<Integer> imgIds = mapper.selectJoinList(Integer.class, new MPJLambdaWrapper<Img>()
                .select(Img::getImgId) // 只查询 imgId
                .eq(img.getUserId() != null, Img::getUserId, img.getUserId())
                .leftJoin(ImgTag.class, ImgTag::getImgId, Img::getImgId)
                .leftJoin(Tag.class, Tag::getTagId, ImgTag::getTagId)
                .leftJoin(ImgPeople.class, ImgPeople::getImgId, Img::getImgId)
                .leftJoin(People.class, People::getPeopleId, ImgPeople::getPeopleId)
                .eq(People::getValid, 1)
                .eq(Tag::getValid, 1)
                .in(!img.getTags().isEmpty(), Tag::getTagName, img.getTags())
                .in(!img.getPeoples().isEmpty(), People::getName, img.getPeoples())
                .groupBy(Img::getImgId)
                .having(!img.getPeoples().isEmpty() && !img.getTags().isEmpty(),
                        "COUNT(DISTINCT tag_name) = {0} AND COUNT(DISTINCT name) = {1}",
                        img.getTags().size(), img.getPeoples().size())
                .having(!img.getPeoples().isEmpty() && img.getTags().isEmpty(),
                        "COUNT(DISTINCT name) = {0}", img.getPeoples().size())
                .having(!img.getTags().isEmpty() && img.getPeoples().isEmpty(),
                        "COUNT(DISTINCT tag_name) = {0}", img.getTags().size())
                .orderByAsc(Img::getImgId)
                .last(offset != -1, "LIMIT " + offset + ", " + limit) // MySQL 分页
        );
        if (imgIds.isEmpty()) {
            return new ArrayList<>(); // 如果没有符合条件的图片，直接返回空列表
        }

        // 2️⃣ 查询完整的图片信息，并收集 tagNames 和 peopleNames
        List<ImgDTO> images = mapper.selectJoinList(ImgDTO.class, new MPJLambdaWrapper<Img>()
                .selectAll(Img.class) // 查询所有图片字段
                .select(Tag::getTagName)     // 查询 tagName
                .select(People::getName)     // 查询 peopleName
                .leftJoin(ImgTag.class, ImgTag::getImgId, Img::getImgId)
                .leftJoin(Tag.class, Tag::getTagId, ImgTag::getTagId)
                .leftJoin(ImgPeople.class, ImgPeople::getImgId, Img::getImgId)
                .leftJoin(People.class, People::getPeopleId, ImgPeople::getPeopleId)
                .in(Img::getImgId, imgIds) // 仅查询符合条件的图片
        );

        // 3️⃣ 处理 tagNames 和 peopleNames
        Map<Integer, ImgDTO> imgMap = new HashMap<>();
        for (ImgDTO image : images) {
            imgMap.computeIfAbsent(image.getImgId(), k -> {
                ImgDTO dto = new ImgDTO();
                dto.setImgId(image.getImgId());
                dto.setImgName(image.getImgName());
                dto.setUserId(image.getUserId());
                dto.setTags(new ArrayList<>());
                dto.setPeoples(new ArrayList<>());
//                String imgName = String.format("%d.jpeg", image.getImgId());
//                dto.setImg(minioUtilS.download(imgName, ImgPath));
                return dto;
            });

            // 收集 tagName
            if (image.getTagName() != null) {
                if (!imgMap.get(image.getImgId()).getTags().contains(image.getTagName()))
                    imgMap.get(image.getImgId()).getTags().add(image.getTagName());
            }
            // 收集 peopleName
            if (image.getName() != null) {
                if (!imgMap.get(image.getImgId()).getPeoples().contains(image.getName()))
                    imgMap.get(image.getImgId()).getPeoples().add(image.getName());
            }
        }

        return new ArrayList<>(imgMap.values());
    }

//    @Override
//    public List<ImgDTO> addImg(ImgDTO img){
//        Img img_in=new Img(img);
//
//    }
}
