package pl.softlink.spellbinder.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.logging.Logger;

import org.json.JSONObject;
import pl.softlink.spellbinder.client.connection.ClientRunnable;
import pl.softlink.spellbinder.service.TextDiff;


public class FrontController {

    private String content = "";

    @FXML
    private Pane root;

    @FXML
    protected void onKeyReleased(KeyEvent keyEvent) {

        String newContent = ((TextArea) root.getChildren().get(0)).getText();
        String diff = TextDiff.generate(content, newContent);

        if (diff != null) {

            JSONObject payloadJson = new JSONObject();
            payloadJson.put("diff", diff);
            String payloadString = payloadJson.toString();
            ClientRunnable.send(payloadString);
        }

        content = TextDiff.apply(content, diff);

        Logger.getAnonymousLogger().info("Pressed key: " + keyEvent.getText());
    }

}