package com.github.musicsnsproject.web.dto.music.cart;


import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long userId;
    private String musicId;
    private String musicName;
    private String albumName;
    private String albumImageUrl;
    private String artistName;

    //private MyUser myUser;
}
