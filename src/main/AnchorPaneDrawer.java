package main;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * Displays buttons on AnchorPane correctly.
 */
public class AnchorPaneDrawer {
    private static final double TOP_DISTANCE = 50.0;
    private static final double LEFT_DISTANCE = 60.0;
    private static final double TOP_INIT = 10.0;
    private static final double LEFT_INIT = 10.0;
    private static final double BORDER_DISTANCE = 80.0;
    private static final String CLICKED_BUTTON_STYLE = " -fx-border-color: black;";
    public static final String BUTTON_STYLE = "-fx-background-color: transparent;";

    private final AnchorPane anchorPane;
    private Button lastClickedButton;
    private Double left;
    private Double top;
    private String lastClickedButtonStyle;

    public AnchorPaneDrawer(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        this.left = LEFT_INIT;
        this.top = TOP_INIT;
    }

    public void putOnAnchorPane(Button button) {
        AnchorPane.setTopAnchor(button, top);
        AnchorPane.setLeftAnchor(button, left);
        anchorPane.getChildren().add(button);
        modifyButtonCoordinates();
    }

    private void modifyButtonCoordinates() {
        if (left + BORDER_DISTANCE > anchorPane.getWidth()) {
            left = LEFT_INIT;
            top += TOP_DISTANCE;
        } else {
            left += LEFT_DISTANCE;
        }
    }

    public void onButtonClicked(Button button) {
        if(lastClickedButton == button) {
            return;
        }
        if (lastClickedButton != null) {
            lastClickedButton.setStyle(lastClickedButtonStyle);
        }
        lastClickedButton = button;
        lastClickedButtonStyle = button.getStyle();
        button.setStyle(button.getStyle() + CLICKED_BUTTON_STYLE);
    }
}
