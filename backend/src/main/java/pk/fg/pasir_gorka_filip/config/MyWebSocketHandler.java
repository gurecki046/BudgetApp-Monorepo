package pk.fg.pasir_gorka_filip.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pk.fg.pasir_gorka_filip.dto.ExpenseNotificationDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String email = (String) session.getAttributes().get("email");
        if (email != null) {
            sessions.put(email, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String email = (String) session.getAttributes().get("email");
        if (email != null) {
            sessions.remove(email);
        }
    }

    public void sendNotification(String userEmail, ExpenseNotificationDTO notification) {
        WebSocketSession session = sessions.get(userEmail);

        if (session != null && session.isOpen()) {
            try {
                // Czyścimy tekst z cudzysłowów, żeby nie zepsuć struktury JSON
                String cleanTitle = notification.getTitle().replace("\"", "'");
                String cleanMsg = notification.getMessage().replace("\"", "'");

                String jsonMessage = "{" +
                        "\"type\":\"" + notification.getType() + "\"," +
                        "\"groupId\":" + notification.getGroupId() + "," +
                        "\"groupName\":\"" + notification.getGroupName() + "\"," +
                        "\"title\":\"" + cleanTitle + "\"," +
                        "\"amount\":" + notification.getAmount() + "," +
                        "\"userShare\":" + notification.getUserShare() + "," +
                        "\"createdByEmail\":\"" + notification.getCreatedByEmail() + "\"," +
                        "\"message\":\"" + cleanMsg + "\"" +
                        "}";

                session.sendMessage(new TextMessage(jsonMessage));
            } catch (Exception e) {
                System.out.println("BLAD WYSYLKI: " + e.getMessage());
            }
        }
    }
}