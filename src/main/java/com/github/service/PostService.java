package com.github.service;

import com.github.dto.PostCreateRequest;
import com.github.entity.PostEntity;
import com.github.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


        @Transactional
        public void create(PostCreateRequest req, MultipartFile image) {
            LocalDateTime now = LocalDateTime.now();

            byte[] imageBytes = null;
            try {
                if (image != null && !image.isEmpty()) {
                    imageBytes = image.getBytes();
                }
            } catch (IOException e) {
                throw new RuntimeException("이미지 처리 실패", e);
            }




            PostEntity e = PostEntity.builder()
                    .subAreaId(req.getSubAreaId())
                    .repoterId(req.getUserId())       // 나중에 리팩토링 필요할수도
                    .title(req.getTitle())
                    .content(req.getContent())
                    .repoterRisk(req.getRepoterRisk())
                    .createdAt(now)
                    .imageBlob(imageBytes)
                    .updatedAt(now)
                    .build();

            postRepository.insert(e);
    }
}
