package com.github.musicsnsproject.repository.jpa.account.user;

import com.github.musicsnsproject.repository.jpa.account.follow.Follow;
import com.github.musicsnsproject.repository.jpa.account.role.Role;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialId;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "users_phone_number_unique", columnNames = "phone_number"),
                @UniqueConstraint(name = "users_email_unique", columnNames = "email")
        })
@DynamicInsert
@Getter
public class MyUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 15, unique = true)
    private String phoneNumber;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String gender = "미정"; // enum으로도 가능

    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String profileImage;

    @Column(nullable = false)
    private LocalDate registeredAt;

    @Column(nullable = false, length = 15)
    private String status = "정상 계정";

    @Column(nullable = false)
    private Integer failureCount = 0;

    private LocalDateTime failureDate;

    private LocalDateTime withdrawalDate;

    @OneToMany(mappedBy = "myUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialId> socialIds;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // 내가 팔로우한 사람들 (내가 follower)
    @OneToMany(mappedBy = "followPk.follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following = new ArrayList<>();

    // 나를 팔로우한 사람들 (내가 followee)
    @OneToMany(mappedBy = "followPk.followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();



    public static MyUser onlyId(Long userId) {
        MyUser myUser = new MyUser();
        myUser.userId = userId;
        return myUser;
    }
}
