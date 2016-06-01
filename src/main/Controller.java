package main;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Controller {
    private final static double BUTTON_HEIGHT = 40.0;
    private final static double BUTTON_WIDTH = 40.0;
    private final static double CUT_WIDTH = 20.0f;
    private final static Font BUTTON_FONT = new Font(12);
    private static final int HCD_NUMBER = 1;
    private static final int ETD_NUMBER = 2;
    private static final boolean MAIN_CUT = true;
    private static final boolean REV_CUT = false;

    private AnchorPaneDrawer apDrawer;
    private List<HBox> boxList = new ArrayList<>();
    private List<ContextMenu> contextMenusB = new ArrayList<>();
    private List<ContextMenu> contextMenusC = new ArrayList<>();
    private List<ContextMenu> contextMenusY = new ArrayList<>();
    private List<ContextMenu> contextMenusZ = new ArrayList<>();
    private FileChooser fileChooser = new FileChooser();
    private Preferences prefs = Preferences.userNodeForPackage(Controller.class);
    private File directory = new File(prefs.get("directoryPath", "C:\\"));

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView<Text> fileList;

    //Exits the app.
    public void onCloseMenuItemClick(Event event) {
        Platform.exit();
    }

    //Allows user select file with spectre and loads spectre from it to data.
    public void onAddSpectreMenuItemClick(ActionEvent actionEvent) {
        makeActionWithFile(file -> {
            try {
                Spectre spectre = Data.addSpectreFromFile(file.toPath());
                String additionalInfo = spectre.getTechnology().toString();
                addFileToList(spectre.getFileNumber(), spectre.getFileName(), additionalInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Puts information about loaded spectres on screen.
    private void addFileToList(int fileNumber, String fileName, String additionalInfo) {
        Text text = new Text("file "
                + String.valueOf(fileNumber)
                + ": "
                + fileName
                + ", "
                + additionalInfo);
        ObservableList<Text> list = fileList.getItems();
        list.add(text);
        fileList.setItems(list);
    }

    //Deletes all spectres from Data.
    public void onDeleteAllSpectresMenuItemClick(ActionEvent actionEvent) {
        Data.deleteAllSpectres();
    }

    //Loads candidate sequence from file and saves to data.
    public void onSetCandidateSequenceMenuItemClick(ActionEvent actionEvent) {
        makeActionWithFile(file -> {
            try {
                Data.addCandidateSequenceFromFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //Chooses file and perform some action with it.
    private void makeActionWithFile(Consumer<File> action) {
        fileChooser.setInitialDirectory(directory);
        File chosenFile = fileChooser.showOpenDialog(null);
        if (chosenFile != null) {
            directory = chosenFile.getParentFile();
            prefs.put("directoryPath", directory.getPath());
            action.accept(chosenFile);
        }
    }

    //Compares candidate sequence theoretical spectre and experiment results and shows result of comparing.
    public void onCompareButtonClick(ActionEvent actionEvent) {
        anchorPane.getChildren().clear();
        boxList = new ArrayList<>();
        contextMenusB = new ArrayList<>();
        apDrawer = new AnchorPaneDrawer(anchorPane);
        AminoAcidHolder acidHolder = new AminoAcidHolder();
        for(Character c: Data.getCandidateSequence().toCharArray()) {
            if(acidHolder.getAcidLetters().contains(c)) {
                apDrawer.putOnAnchorPane(createCandidateSequenceBox(c.toString()));
            }
        }
        if(Data.getHCDSpectres() != null) {
            Data.getHCDSpectres().forEach(spectre -> confirmCuts(spectre,
                    Data.getCandidateHCDCutList(),
                    Data.getReversedHCDCutList(),
                    contextMenusB,
                    contextMenusY));
        }
        if(Data.getETDSpectres() != null) {
            Data.getETDSpectres().forEach(spectre -> confirmCuts(spectre,
                    Data.getCandidateETDCutList(),
                    Data.getReversedETDCutList(),
                    contextMenusC,
                    contextMenusZ));
        }
        showConfirmedCuts(contextMenusB, HCD_NUMBER, MAIN_CUT);
        showConfirmedCuts(contextMenusY, HCD_NUMBER, REV_CUT);
        showConfirmedCuts(contextMenusC, ETD_NUMBER, MAIN_CUT);
        showConfirmedCuts(contextMenusZ, ETD_NUMBER, REV_CUT);
    }

    private void confirmCuts(Spectre spectre, List<Double> candidateCuts,
                             List<Double> reversedCuts,
                             List<ContextMenu> cuts,
                             List<ContextMenu> reversed) {
        List<Set<Peak>> confirmed = spectre.confirmSequence(candidateCuts);
        addConfirmedCuts(confirmed, spectre.getFileNumber(), cuts);
        confirmed = spectre.confirmSequence(reversedCuts);
        Collections.reverse(confirmed);
        addConfirmedCuts(confirmed, spectre.getFileNumber(), reversed);
    }

    private void showConfirmedCuts(List<ContextMenu> contextMenus, int techNumber, boolean mainCut) {
        for(int i = 0; i < boxList.size(); i++) {
            final int finalI = i;
            Pane cutImage = (Pane)boxList.get(i).getChildren().get(techNumber);
            ObservableList<Node> children = cutImage.getChildren();
            List<Line> lines;
            if (mainCut) {
                lines = Arrays.asList((Line)children.get(0), (Line)children.get(1));
            } else {
                lines = Arrays.asList((Line)children.get(2), (Line)children.get(3));
            }

            cutImage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                   if(event.getY() < BUTTON_HEIGHT/2 && mainCut) {
                        contextMenus.get(finalI).show(cutImage, event.getScreenX(), event.getScreenY());
                    }
                    if(event.getY() >= BUTTON_HEIGHT/2 && !mainCut) {
                        contextMenus.get(finalI).show(cutImage, event.getScreenX(), event.getScreenY());
                    }
                }
            });

            lines.forEach(line -> {
                if(contextMenus.get(finalI).getItems().size() != 0) {
                    Paint value = (techNumber == HCD_NUMBER) ? Color.DARKGREEN : Color.DARKBLUE;
                    line.setStroke(value);
                    line.setStrokeWidth(3.0);
                }
            });
        }
    }

    private void addConfirmedCuts(List<Set<Peak>> confirmed, int fileNumber, List<ContextMenu> cuts) {
        for(int i = 0; i < confirmed.size() && i < cuts.size(); i++) {
            if(confirmed.get(i).size() != 0) {
                Double average = confirmed.get(i).stream()
                        .collect(Collectors.averagingDouble(Peak::getMass));
                Menu menu = new Menu(Integer.toString(fileNumber) + " file, "
                        + Integer.toString(confirmed.get(i).size())
                        + " peaks, mass: " + average.toString());
                for(Peak p : confirmed.get(i)) {
                    menu.getItems().add(new MenuItem(p.print()));
                }
                ContextMenu cm = cuts.get(i);
                cm.getItems().add(menu);
            }
        }
    }

    //Creates button with amino acid letter on it and two cuts and put them to HBox.
    private HBox createCandidateSequenceBox(String letter) {
        Button button = new Button(letter);
        button.setFont(BUTTON_FONT);
        button.setMaxSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setStyle(AnchorPaneDrawer.BUTTON_STYLE);
        button.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> button.setEffect(new DropShadow()));
        button.addEventFilter(MouseEvent.MOUSE_EXITED, event -> button.setEffect(null));
        HBox hbox = new HBox(button, createCutImage(), createCutImage());
        hbox.setSpacing(0.0f);
        boxList.add(hbox);
        contextMenusB.add(new ContextMenu());
        contextMenusC.add(new ContextMenu());
        contextMenusY.add(new ContextMenu());
        contextMenusZ.add(new ContextMenu());
        button.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() != MouseButton.SECONDARY) {
                apDrawer.onButtonClicked(button);
            }
        });
        return hbox;
    }

    //Creates structure, which allows to show different types of cuts.
    private Pane createCutImage() {
        Pane pane = new Pane();
        pane.setPrefSize(CUT_WIDTH, BUTTON_HEIGHT);
        pane.getChildren().addAll(
                createLine(0.0f, 0.0f, 10.0f, 0.0f),
                createLine(10.0f, 0.0f, 10.0f, BUTTON_HEIGHT /2),
                createLine(10.0f, BUTTON_HEIGHT /2, 10.0f, BUTTON_HEIGHT),
                createLine(10.0f, BUTTON_HEIGHT, 20.0f, BUTTON_HEIGHT));
        return pane;
    }

    private Line createLine(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.TRANSPARENT);
        return line;
    }

}
