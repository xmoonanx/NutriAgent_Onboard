package com.NurtiAgent.Onboard.service;

import com.NurtiAgent.Onboard.entity.User;
import com.NurtiAgent.Onboard.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 게스트 세션을 생성하고 DB에 저장한 후 게스트 ID를 반환
     * @param request HTTP 요청 (세션 생성용)
     * @return 생성된 게스트 ID
     */
    @Transactional
    public String saveUser(HttpServletRequest request) {
        // 고유한 게스트 ID 생성
        String guestId = generateUniqueGuestId();

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .guestId(guestId)
                .build();

        User savedUser = userRepository.save(user);

        // 세션에 userId 저장
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", savedUser.getId().toString());

        return guestId;
    }

    /**
     * 게스트 ID로 세션을 갱신
     * @param request HTTP 요청 (세션 갱신용)
     * @param guestId 게스트 ID
     */
    @Transactional
    public void renewSession(HttpServletRequest request, String guestId) {
        // 게스트 ID로 사용자 조회
        User user = userRepository.findByGuestId(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid guest ID: " + guestId));

        // lastAccessedAt 업데이트 (@PreUpdate에서 자동 처리)
        userRepository.save(user);

        // 세션 갱신
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getId().toString());
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
