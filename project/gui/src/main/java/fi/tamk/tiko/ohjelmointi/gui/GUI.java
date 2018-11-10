package fi.tamk.tiko.ohjelmointi.gui;

import fi.tamk.tiko.ohjelmointi.json.*;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;

/**
 * Class that, constructs the GUI for Shopping List application.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class GUI extends Application {

    /**
     * @see javafx.application.Application#start(javafx.stage.Stage) start
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Shopping List App");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.show();
    }
}