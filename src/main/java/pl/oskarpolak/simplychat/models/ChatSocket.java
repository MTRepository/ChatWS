package pl.oskarpolak.simplychat.models;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EnableWebSocket
@Component
public class ChatSocket extends TextWebSocketHandler implements WebSocketConfigurer {

    private List<UserModel> sessionList = new ArrayList<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this, "/chat").setAllowedOrigins("*");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UserModel userModel = findUserBySessionId(session.getId());
        String messageString = message.getPayload();

        if(userModel.getUsername() == null){
            userModel.setUsername(messageString);
            userModel.sendMessage("server:Ustawiłem nick");
            return;
        }

        sendMessageToAll("log:" + userModel.getUsername() + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserModel userModel = new UserModel(session);
        sessionList.add(userModel);

        userModel.sendMessage("server:Witaj na naszym chacie!");
        userModel.sendMessage("server:Twoja pierwsza wiadomość, zostanie Twoim nickiem");

        sendMessageToAll("connected:" + sessionList.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionList.remove(findUserBySessionId(session.getId()));

        sendMessageToAll("connected:" + sessionList.size());
    }

    private void sendMessageToAll(String message) throws IOException {
        for (UserModel userModel : sessionList) {
            userModel.sendMessage(message);
        }
    }

    private UserModel findUserBySessionId(String sessionId){
        return sessionList.stream()
                .filter(s -> s.getSession().getId().equals(sessionId))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }
}
