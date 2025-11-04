package com.NurtiAgent.Onboard.service;

import com.NurtiAgent.Onboard.entity.User;
import com.NurtiAgent.Onboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestSessionService {

    private final UserRepository userRepository;

    /**
     * 새로운 게스트 세션을 생성하고 DB에 저장
     * @return 생성된 게스트 ID
     */
    @Transactional
    public String createGuestSession() {
        String guestId = generateUniqueGuestId();

        User user = User.builder()
                .guestId(guestId)
                .build();

        userRepository.save(user);

        return guestId;
    }

    /**
     * 게스트 ID로 사용자를 조회하고 lastAccessedAt을 업데이트
     * @param guestId 게스트 ID
     * @return User 엔티티
     */
    @Transactional
    public User validateAndUpdateSession(String guestId) {
        User user = userRepository.findByGuestId(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid guest session"));

        // lastAccessedAt은 @PreUpdate에서 자동으로 업데이트됨
        return userRepository.save(user);
    }

    /**
     * 게스트 ID가 유효한지 확인
     * @param guestId 게스트 ID
     * @return 유효 여부
     */
    public boolean isValidGuestSession(String guestId) {
        return userRepository.existsByGuestId(guestId);
    }

    /**
     * 중복되지 않는 고유한 게스트 ID 생성
     * @return 고유한 게스트 ID
     */
    private String generateUniqueGuestId() {
        String guestId;
        do {
            guestId = "guest_" + UUID.randomUUID().toString();
        } while (userRepository.existsByGuestId(guestId));
        return guestId;
    }
}
