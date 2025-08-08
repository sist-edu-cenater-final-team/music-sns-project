package com.github.musicsnsproject.repository.jpa.account.history.login;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Table(name = "login_history")
@Entity
@Getter
@DynamicInsert
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginHistoryId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private MyUser myUser;

    @Column(nullable = false, length = 50)
    private String ipAddress;

    private LocalDateTime loggedAt;

    public static LoginHistory of(MyUser myUser, String ipAddress){
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.myUser = myUser;
        loginHistory.ipAddress = ipAddress;
        return loginHistory;
    }
}
