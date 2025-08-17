package com.github.musicsnsproject.web.dto.music.cart;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private String musicName;
    private String musicArtist;
    private String musicAlbum;
    private String musicPrice;
}
