package com.github.controller;

import com.github.dto.PostCreateRequest;
import com.github.entity.PostEntity;
import com.github.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {


    private final PostService postService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(
            @ModelAttribute PostCreateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        postService.create(req, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostEntity>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0")int size

    ) {
        return ResponseEntity.ok(postService.getAllPosts(page,size));
    }

}
