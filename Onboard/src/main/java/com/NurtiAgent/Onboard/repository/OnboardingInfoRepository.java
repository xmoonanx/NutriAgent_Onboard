    package com.NurtiAgent.Onboard.repository;

import com.NurtiAgent.Onboard.entity.OnboardingInfo;
import com.NurtiAgent.Onboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingInfoRepository extends JpaRepository<OnboardingInfo, Long> {

    /**
     * 사용자로 온보딩 정보 조회
     */
    Optional<OnboardingInfo> findByUser(User user);

    /**
     * 사용자 ID로 온보딩 정보 조회
     */
    Optional<OnboardingInfo> findByUserId(Long userId);

    /**
     * 사용자로 온보딩 정보 존재 여부 확인
     */
    boolean existsByUser(User user);
}
