package com.kyleaheron;

import com.kyleaheron.gui.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
TODO: Review all previous code, including the api, and replace ifs with assertions if applicable.
TODO: Finish making all of the settings control panels.
TODO: Make api do a more concise check on the username.json, not just if its present.
TODO: Make the application settings panel.
TODO: Skins? Make use of.
TODO: Static implementation of the the light object, or just the show, and output buffer to avoid sending too many commands.
TODO: Switch the light controler to a tree map system of storing the variables, so the repetitive stuff can be looped @see GuiController.createEffectControlPanel()
 */

public class Main extends Application  {

    public static void main (String[] args) {
        System.setProperty("typoGraphica", "TypoGraphica");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(GuiController.class.getResource("ControllerGui.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}
