package com.github.controller;

import com.github.dto.ProfileResponse;
import com.github.dto.ProfileUpdateRequest;
import com.github.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
