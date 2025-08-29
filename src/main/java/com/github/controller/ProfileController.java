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
    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @RequestParam int userId,
            @RequestBody ProfileUpdateRequest req
    ) {
        return ResponseEntity.ok(profileService.updateMyProfile(userId, req));
    }

    // 프로필 사진 업로드
    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam int userId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        String url = profileService.uploadProfilePhoto(userId, file);
        // 간단히 업로드된 URL만 반환
        return ResponseEntity.ok().body(java.util.Map.of("profilePhotoUrl", url));
    }

}
