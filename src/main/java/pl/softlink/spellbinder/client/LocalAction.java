package pl.softlink.spellbinder.client;

import org.json.JSONObject;
import pl.softlink.spellbinder.client.event.DocumentChangedLocallyEvent;
import pl.softlink.spellbinder.global.connection.Connection;
import pl.softlink.spellbinder.global.event.EventListener;

public class LocalAction implements EventListener<DocumentChangedLocallyEvent> {

    private Connection connection;

    public LocalAction(Connection connection) {
        this.connection = connection;
    }

    public void onEvent(DocumentChangedLocallyEvent event) {
        JSONObject payloadJson = new JSONObject();
        payloadJson.put("action", "patch");
        payloadJson.put("connection_id", connection.getConnectionId());
        payloadJson.put("document_id", event.getDocument().getDocumentId());
        payloadJson.put("diff", event.getDiff());
        String payloadString = payloadJson.toString();
        connection.push(payloadString);
    }

}
