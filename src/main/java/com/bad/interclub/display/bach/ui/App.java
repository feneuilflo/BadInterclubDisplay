package com.bad.interclub.display.bach.ui;

import com.bad.interclub.display.bach.model.Interclub;
import com.bad.interclub.display.bach.model.MatchOrderUtils;
import com.bad.interclub.display.bach.xls.XlsLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class App extends Application{

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static Interclub model;

    public static Interclub getModelInstance() {
        return model;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        LOGGER.info("start");

        // init model
        initModel();

        // build nodes
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Root.fxml")));
        Scene scene = new Scene(root);

        // init css
        String cssURL = "css/app.css";
        String css = Objects.requireNonNull(getClass().getClassLoader().getResource(cssURL)).toExternalForm();
        scene.getStylesheets().add(css);

        // build stage
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icon.png"))));
        primaryStage.setScene(scene);

        // show
        primaryStage.show();

        //
        Parent matchOrderRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/matchorder/Root.fxml")));
        Scene matchOrderScene = new Scene(matchOrderRoot, 600, 400);
        matchOrderScene.getStylesheets().add(css);

        Stage matchOrderStage = new Stage();
        matchOrderStage.initOwner(primaryStage);
        matchOrderStage.initModality(Modality.APPLICATION_MODAL);
        //matchOrderStage.initStyle(StageStyle.UNDECORATED);
        matchOrderStage.centerOnScreen();
        matchOrderStage.setScene(matchOrderScene);


        matchOrderStage.show();
    }


    private void initModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez le fichier à charger");
        fileChooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Excel file", "*.xls"),
                new FileChooser.ExtensionFilter("All files", "*")
        );

        //File file = fileChooser.showOpenDialog(null);
        File file = new File("F:\\BadInterclubDisplay\\example\\J04260N11.xls");

        if(file == null) {
            LOGGER.error("No file chosen --> exit");
            System.exit(1);
        } else {
            String extension = file.getName().substring(file.getName().lastIndexOf('.'));
            if(extension.equalsIgnoreCase(".xls")) {
                try {
                    Interclub interclub = XlsLoader.loadFromFile(file);

                    // match order
                    if(interclub.getMatchOrder().isEmpty()) {
                        interclub.getMatchOrder().setAll(MatchOrderUtils.getBestMatchOrder(interclub));
                    }

                    LOGGER.info("Match order: {}", interclub.getMatchOrder());

                    // listen for file modification
                    Thread thread = new Thread(() -> listenForFileModifcation(file));
                    thread.setDaemon(true);
                    thread.setName("FileChangeListener");
                    thread.start();

                    App.model = interclub;
                } catch (IOException e) {
                    LOGGER.error("Error while parsing file {}: ", file.getAbsolutePath(), e);
                    System.exit(1);
                }
            }
        }
    }

    private void listenForFileModifcation(File file) {
        try {
            // create watch service
            WatchService watchService
                    = FileSystems.getDefault().newWatchService();

            // get directory
            Path path = file.getParentFile().toPath();

            // register watch service for file changes
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            // listen for file change
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind:" + event.kind()
                                    + ". File affected: " + event.context() + ".");
                    if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY
                        && path.resolve((Path) event.context()).toString().equals(file.toPath().toString())) {
                        LOGGER.info("WatchService - File updated !");
                        // update model
                        Platform.runLater(() -> {
                            try {
                                XlsLoader.updateWithFile(App.model, file);
                            } catch (IOException e) {
                                LOGGER.error("An exception occurred while parsing file: ", e);
                            }
                        });
                    }

                }
                key.reset();
            }
        } catch(Exception e) {
            LOGGER.error("An exception occurred while listening for file changes: ", e);
        }
    }
}
