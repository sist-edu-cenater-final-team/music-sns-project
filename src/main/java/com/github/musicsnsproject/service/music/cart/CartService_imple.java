package com.github.musicsnsproject.service.music.cart;

import com.github.musicsnsproject.repository.jpa.music.cart.MusicCartRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService_imple implements CartService {

    private final MusicCartRepository musicCartRepository;
    private final JPAQueryFactory jpaQueryFactory;

}
