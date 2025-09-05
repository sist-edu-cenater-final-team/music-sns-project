package com.github.musicsnsproject.web.interceptors;

import com.github.musicsnsproject.config.security.JwtProvider;
import com.github.musicsnsproject.config.web.principal.StompPrincipal;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Objects;

import static com.github.musicsnsproject.config.security.JwtProvider.authHeaderToToken;

public class JwtChannelInterceptor implements ChannelInterceptor {

    private final static String PRINCIPAL_HEADER_NAME = "principal";

    @Override
    public Message<?> preSend(@NotNull Message<?> message,
                              @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = Objects.requireNonNull(accessor).getCommand();
        if (StompCommand.CONNECT.equals(command)) {
            String principal = accessor.getFirstNativeHeader(PRINCIPAL_HEADER_NAME);
            StompPrincipal stompPrincipal = StompPrincipal.of(principal);
            accessor.setUser(stompPrincipal);
        }
        return message;
    }
}
