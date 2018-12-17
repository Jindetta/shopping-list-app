package fi.tamk.tiko.ohjelmointi.gui;

import static javafx.application.Application.launch;

/**
 * Launches the JavaFX application.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class Launcher {

    /**
     * Starts the program.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Author: Joonas Lauhala");

        launch(GUI.class, args);
    }
}