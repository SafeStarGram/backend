package com.github.controller;

import com.github.dto.ProfileResponse;
import com.github.dto.ProfileUpdateRequest;
import com.github.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@RequestParam int userId) {
        return ResponseEntity.ok(profileService.getMyProfile(userId));
    }

    // 내 프로필 수정
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @RequestParam int userId,
            @RequestPart(value = "profile", required = false) ProfileUpdateRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(
                profileService.updateMyProfileAndPhoto(userId, req, file)
        );
    }
}
