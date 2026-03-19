package com.example.bookmanagementsystembo.user.entity;

import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import com.example.bookmanagementsystembo.user.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 사용자 엔티티.
 * Soft Delete 적용: DELETE 대신 is_deleted = true 로 논리 삭제.
 * 조회 시 is_deleted = false 인 레코드만 자동 포함.
 */
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id = ?")
@Where(clause = "is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Comment("사용자 ID")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "password")
    @Comment("비밀번호")
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    @Comment("사용자 이름")
    private String name;

    @Column(name = "department_id")
    @Comment("부서 ID")
    private Long departmentId;

    @Column(name = "profile_image", length = 500)
    @Comment("프로필 이미지 URL")
    private String profileImage;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Comment("권한")
    private Role role;

    @Column(name = "login_fail_count", nullable = false)
    @Comment("로그인 실패 횟수")
    private Integer loginFailCount = 0;

    @Column(name = "is_locked", nullable = false)
    @Comment("계정 잠금 여부")
    private boolean isLocked = false;

    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    public static Users create(String email, String password, String name, Long departmentId, String profileImage, Role role) {
        return new Users(null, email, password, name, departmentId, profileImage, role, 0, false);
    }

    public void incrementLoginFailCount() {
        this.loginFailCount++;
        if (this.loginFailCount >= MAX_LOGIN_FAIL_COUNT) {
            this.isLocked = true;
        }
    }

    public void resetLoginFailCount() {
        this.loginFailCount = 0;
        this.isLocked = false;
    }

    public void updateProfile(String name, String profileImage) {
        if (name != null) {
            this.name = name;
        }
        if (profileImage != null) {
            this.profileImage = profileImage;
        }
    }

}
