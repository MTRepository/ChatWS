package pl.oskarpolak.simplychat.models;

import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Data
public class UserModel {
    private String username;
    private WebSocketSession session;

    public UserModel(WebSocketSession session){
        this.session = session;
    }

    public void sendMessage(String text) throws IOException {
        session.sendMessage(new TextMessage(text));
    }
}
