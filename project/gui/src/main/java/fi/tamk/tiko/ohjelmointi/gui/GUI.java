package fi.tamk.tiko.ohjelmointi.gui;

import fi.tamk.tiko.ohjelmointi.json.*;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.layout.BorderPane;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;

import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.FileWriter;

/**
 * Constructs the GUI for Shopping List application.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class GUI extends Application {

    /**
     * Stores list items.
     */
    private ObservableList<Item> items;

    /**
     * Saves data to a file.
     */
    private void saveToFile() {
        try (JSONWriter json = new JSONWriter(new FileWriter("list.json"))) {
            JSONArray array = new JSONArray();

            for (Item item : items) {
                JSONObject object = new JSONObject();

                object.putString("item", item.getItemName());
                object.putNumber("amount", (long) item.getItemAmount());

                array.addObject(object);
            }

            json.writeArray(array);
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("Cannot save file");
            alert.setContentText(e.getMessage());
            alert.setHeaderText(null);

            alert.show();
        }
    }

    /**
     * Creates menu bar.
     * @return MenuBar.
     */
    private MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();

        Menu file = new Menu("File");

        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> saveToFile());

        SeparatorMenuItem divider = new SeparatorMenuItem();

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());

        file.getItems().addAll(save, divider, exit);

        Menu edit = new Menu("Edit");
        Menu about = new Menu("About");

        menu.getMenus().addAll(file, edit, about);

        return menu;
    }

    /**
     * Creates table view.
     * @return TableView.
     */
    private TableView<Item> createTableView() {
        TableView<Item> table = new TableView<>();

        TableColumn<Item, String> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(new PropertyValueFactory<>("itemAmount"));
        table.getColumns().add(amount);

        TableColumn<Item, Integer> item = new TableColumn<>("Item");
        item.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        table.getColumns().add(item);

        // TODO: Load items or use default view
        table.setItems(items);

        return table;
    }

    /**
     * Creates initial scene.
     * @return Scene.
     */
    private Scene createSceneContainer() {
        BorderPane panel = new BorderPane();

        panel.setTop(createMenuBar());
        panel.setCenter(createTableView());
        panel.setBottom(new Label("Author: Joonas Lauhala"));

        return new Scene(panel);
    }

    /**
     * @see Application#init init
     */
    @Override
    public void init() {
        items = FXCollections.observableArrayList(
            new Item(0, "Item 1"),
            new Item(0, "Item 2")
        );
    }

    /**
     * @see Application#start(Stage) start
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Shopping List App");
        stage.setScene(createSceneContainer());
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.centerOnScreen();
        stage.show();
    }
}