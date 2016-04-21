package main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Controller {
    private final static String OPEN_FILE = "Открыть файл";
    private final static String CUT_CONFIRMED_STYLE = "-fx-background-color: green;";
    private final static double BUTTON_SIZE = 55.0;
    private final static Font BUTTON_FONT = new Font(20);

    private AnchorPaneDrawer apDrawer;
    private List<Button> buttonList = new ArrayList<>();
    private List<ContextMenu> contextMenus = new ArrayList<>();

    @FXML
    private AnchorPane anchorPane;

    public void onCloseMenuItemClick(Event event) {
        Platform.exit();
    }

    public void onAddSpectreMenuItemClick(ActionEvent actionEvent) {
        makeActionWithFile(file -> {
            try {
                Data.addSpectreFromFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onDeleteAllSpectresMenuItemClick(ActionEvent actionEvent) {
        Data.deleteAllSpectres();
    }

    public void onSetCandidateSequenceMenuItemClick(ActionEvent actionEvent) {
        makeActionWithFile(file -> {
            try {
                Data.addCandidateSequenceFromFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void makeActionWithFile(Consumer<File> action) {
        JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showDialog(null, OPEN_FILE);
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            action.accept(file);
        }
    }

    public void onCompareButtonClick(ActionEvent actionEvent) {
        apDrawer = new AnchorPaneDrawer(anchorPane);
        for(Character c: Data.getCandidateSequence().toCharArray()) {
            apDrawer.putOnAnchorPane(createCandidateSequenceButton(c.toString()));
        }
        Data.getSpectreList().forEach(spectre -> {
            List<Set<Peak>> confirmed = spectre.confirmSequence(Data.getCandidateCutList());
            addConfirmedCuts(confirmed, spectre.getFileNumber());
            confirmed = spectre.confirmSequence(Data.getReversedCutList());
            Collections.reverse(confirmed);
            addConfirmedCuts(confirmed, spectre.getFileNumber());});
        setButtonsHandlers();
    }

    private void setButtonsHandlers() {
        for(int i = 0; i < buttonList.size(); i++) {
            final int finalI = i;
            buttonList.get(i).addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if(contextMenus.get(finalI).getItems().size() != 0 && event.getButton() == MouseButton.SECONDARY) {
                    contextMenus.get(finalI).show(buttonList.get(finalI), event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    private void addConfirmedCuts(List<Set<Peak>> confirmed, int fileNumber) {
        for(int i = 0; i < confirmed.size(); i++) {
            if(confirmed.get(i).size() != 0) {
                Double average = confirmed.get(i).stream()
                        .collect(Collectors.averagingDouble(Peak::getMass));
                Menu menu = new Menu(Integer.toString(fileNumber) + " file, "
                        + Integer.toString(confirmed.get(i).size())
                        + " peaks, mass: " + average.toString());
                for(Peak p : confirmed.get(i)) {
                    menu.getItems().add(new MenuItem(p.print()));
                }
                ContextMenu cm = contextMenus.get(i);
                cm.getItems().add(menu);
                buttonList.get(i).setStyle(CUT_CONFIRMED_STYLE);
            }
        }
    }

    private Button createCandidateSequenceButton(String letter) {
        Button button = new Button(letter);
        button.setFont(BUTTON_FONT);
        button.setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
        button.setStyle(AnchorPaneDrawer.BUTTON_STYLE);
        button.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> button.setEffect(new DropShadow()));
        button.addEventFilter(MouseEvent.MOUSE_EXITED, event -> button.setEffect(null));
        buttonList.add(button);
        contextMenus.add(new ContextMenu());
        button.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() != MouseButton.SECONDARY) {
                apDrawer.onButtonClicked(button);
            }
        });
        return button;
    }
}
