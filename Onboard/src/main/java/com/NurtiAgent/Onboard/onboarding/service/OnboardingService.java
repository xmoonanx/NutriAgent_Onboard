package com.NurtiAgent.Onboard.onboarding.service;

import com.NurtiAgent.Onboard.onboarding.dto.OnboardingRequest;
import com.NurtiAgent.Onboard.onboarding.dto.OnboardingResponse;
import com.NurtiAgent.Onboard.onboarding.entity.OnboardingInfo;
import com.NurtiAgent.Onboard.user.entity.User;
import com.NurtiAgent.Onboard.onboarding.repository.OnboardingInfoRepository;
import com.NurtiAgent.Onboard.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingInfoRepository onboardingInfoRepository;
    private final UserRepository userRepository;


// 온보딩 정보 저장 (신규 생성 또는 업데이트)
// @param request HTTP 요청 (세션에서 userId 추출)
//  @param onboardingRequest 온보딩 정보 요청 DTO
// @return 저장된 온보딩 정보 응답 DTO

    @Transactional
    public OnboardingResponse saveOnboardingInfo(HttpServletRequest request, OnboardingRequest onboardingRequest) {
        // 세션에서 userId 추출
        Long userId = getUserIdFromSession(request);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId: " + userId));

        // 기존 온보딩 정보가 있는지 확인
        OnboardingInfo onboardingInfo = onboardingInfoRepository.findByUser(user)
                .orElse(null);

        if (onboardingInfo == null) {
            // 신규 생성
            onboardingInfo = OnboardingInfo.builder()
                    .user(user)
                    .age(onboardingRequest.age())
                    .gender(onboardingRequest.gender())
                    .height(onboardingRequest.height())
                    .weight(onboardingRequest.weight())
                    .healthGoal(onboardingRequest.healthGoal())
                    .activityLevel(onboardingRequest.activityLevel())
                    .exerciseFrequency(onboardingRequest.exerciseFrequency())
                    .exerciseTime(onboardingRequest.exerciseTime())
                    .mealPattern(onboardingRequest.mealPattern())
                    .preferredFoods(onboardingRequest.preferredFoods() != null
                            ? new ArrayList<>(onboardingRequest.preferredFoods())
                            : new ArrayList<>())
                    .dislikedFoods(onboardingRequest.dislikedFoods() != null
                            ? new ArrayList<>(onboardingRequest.dislikedFoods())
                            : new ArrayList<>())
                    .healthConditions(onboardingRequest.healthConditions() != null
                            ? new ArrayList<>(onboardingRequest.healthConditions())
                            : new ArrayList<>())
                    .build();
        } else {
            // 기존 정보 업데이트
            onboardingInfo.setAge(onboardingRequest.age());
            onboardingInfo.setGender(onboardingRequest.gender());
            onboardingInfo.setHeight(onboardingRequest.height());
            onboardingInfo.setWeight(onboardingRequest.weight());
            onboardingInfo.setHealthGoal(onboardingRequest.healthGoal());
            onboardingInfo.setActivityLevel(onboardingRequest.activityLevel());
            onboardingInfo.setExerciseFrequency(onboardingRequest.exerciseFrequency());
            onboardingInfo.setExerciseTime(onboardingRequest.exerciseTime());
            onboardingInfo.setMealPattern(onboardingRequest.mealPattern());
            onboardingInfo.setPreferredFoods(onboardingRequest.preferredFoods() != null
                    ? new ArrayList<>(onboardingRequest.preferredFoods())
                    : new ArrayList<>());
            onboardingInfo.setDislikedFoods(onboardingRequest.dislikedFoods() != null
                    ? new ArrayList<>(onboardingRequest.dislikedFoods())
                    : new ArrayList<>());
            onboardingInfo.setHealthConditions(onboardingRequest.healthConditions() != null
                    ? new ArrayList<>(onboardingRequest.healthConditions())
                    : new ArrayList<>());
        }

        OnboardingInfo savedInfo = onboardingInfoRepository.save(onboardingInfo);
        return OnboardingResponse.from(savedInfo);
    }


//  온보딩 정보 조회
//  @param request HTTP 요청 (세션에서 userId 추출)
//  @return 온보딩 정보 응답 DTO

    @Transactional(readOnly = true)
    public OnboardingResponse getOnboardingInfo(HttpServletRequest request) {
        // 세션에서 userId 추출
        Long userId = getUserIdFromSession(request);

        // 온보딩 정보 조회
        OnboardingInfo onboardingInfo = onboardingInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보를 찾을 수 없습니다. userId: " + userId));

        return OnboardingResponse.from(onboardingInfo);
    }

    // 온보딩 정보 삭제

    @Transactional
    public void deleteOnboardingInfo(HttpServletRequest request) {
        // 세션에서 userId 추출
        Long userId = getUserIdFromSession(request);

        // 온보딩 정보 조회
        OnboardingInfo onboardingInfo = onboardingInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보를 찾을 수 없습니다. userId: " + userId));

        // 삭제
        onboardingInfoRepository.delete(onboardingInfo);
    }

// 세션에서 userId 추출

    private Long getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalArgumentException("세션이 존재하지 않습니다. 로그인이 필요합니다.");
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            throw new IllegalArgumentException("세션에 userId가 존재하지 않습니다. 로그인이 필요합니다.");
        }

        return Long.parseLong(userIdObj.toString());
    }
}
