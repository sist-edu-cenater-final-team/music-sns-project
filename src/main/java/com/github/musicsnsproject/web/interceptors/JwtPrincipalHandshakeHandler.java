//package com.github.musicsnsproject.web.interceptors;
//
//import com.github.musicsnsproject.config.security.JwtProvider;
//import com.github.musicsnsproject.config.web.principal.StompPrincipal;
//import lombok.AllArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
//import java.security.Principal;
//import java.util.Map;
//
//import static com.github.musicsnsproject.config.security.JwtProvider.AUTH_HEADER_NAME;
//import static com.github.musicsnsproject.config.security.JwtProvider.authHeaderToToken;
//
//@AllArgsConstructor(staticName = "of")
//public class JwtPrincipalHandshakeHandler extends DefaultHandshakeHandler {
//    private final JwtProvider provider;
//    @Override
//    protected Principal determineUser(@NotNull ServerHttpRequest request,
//                                      @NotNull WebSocketHandler wsHandler,
//                                      @NotNull Map<String, Object> attributes) {
//        String tokenParam = request.getURI().getQuery();
//        String token = tokenParam.startsWith("token=") ?
//                tokenParam.substring(tokenParam.indexOf("=") + 1) :
//                null;
//        if(token!=null){
//            Object userId = provider.getUserIdFromToken(token);
//            return StompPrincipal.of(userId.toString());
//        }
//
//
//        return super.determineUser(request, wsHandler, attributes);
//    }
//}
