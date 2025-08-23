package com.github.controller;

import com.github.dto.ProfileResponse;
import com.github.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 임시: JWT 없이 테스트. ?userId= 로 받기
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@RequestParam int userId) {
        return ResponseEntity.ok(profileService.getMyProfile(userId));
    }

}
