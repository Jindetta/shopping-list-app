package fi.tamk.tiko.ohjelmointi.gui;

import fi.tamk.tiko.ohjelmointi.json.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileReader;
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
     * 
     */
    private Stage window;

    /**
     * 
     */
    private File saveFile;

    /**
     * 
     */
    @FXML
    private TableView<Item> tableView;

    @FXML
    private TableColumn<Item, Boolean> columnMark;

    @FXML
    private TableColumn<Item, Integer> columnAmount;

    @FXML
    private TableColumn<Item, String> columnItem;

    @FXML
    private CheckBox selectAllToggle;

    @FXML
    private void onCreateAction() {
        tableView.setItems(items = FXCollections.observableArrayList());
    }

    @FXML
    private void onOpenAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open shopping list file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            ObservableList<Item> list = loadFromFile(file, false);

            if (list != null) {
                tableView.setItems(items = list);
            }
        }
    }

    @FXML
    private void onSaveAction() {
        saveToFile(saveFile, false);
    }

    @FXML
    private void onSaveAsAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save shopping list data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveFile = file;
            onSaveAction();
        }
    }

    @FXML
    private void onDropboxImportAction() {
        try {
            DropboxManager manager = new DropboxManager();

            new Thread(() -> manager.downloadFile(saveFile)).start();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("File import from Dropbox failed");
            alert.setContentText(e.getMessage());
            alert.setHeaderText(null);

            alert.show();
        }
    }

                try (JSONWriter writer = new JSONWriter(new FileWriter(TOKEN_FILE))) {
                    writer.write(JSONType.createString(token));
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("File export to Dropbox failed");
            alert.setContentText(e.getMessage());
            alert.setHeaderText(null);

            alert.show();
        }
    }

    @FXML
    private void onCloseAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onSelectAllAction() {
        items.forEach(item -> item.setItemMark(selectAllToggle.isSelected()));
    }

    @FXML
    public void initialize() {
        saveFile = new File("list.json");
        items = loadFromFile(saveFile, true);

        columnItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        columnItem.setCellFactory(TextFieldTableCell.forTableColumn());

        columnMark.setCellValueFactory(new PropertyValueFactory<>("itemMark"));
        columnMark.setCellFactory(f -> new CheckBoxTableCell<>());

        columnAmount.setCellValueFactory(new PropertyValueFactory<>("itemAmount"));
        columnAmount.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        tableView.setOnKeyPressed(this::onTableKeyPressEvent);
        tableView.setOnMouseClicked(this::onTableMouseClickEvent);

        tableView.setEditable(true);
        tableView.setItems(items);

        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private void onTableKeyPressEvent(KeyEvent event) {
        TableViewSelectionModel<Item> model = tableView.getSelectionModel();

        switch (event.getCode()) {
            case INSERT:
                items.add(new Item(1, "-"));
                event.consume();
                break;

            case DELETE:
                int index = model.getSelectedIndex();

                if (index != -1) {
                    items.remove(index);
                }

                event.consume();
                break;

            case PAGE_UP:
                model.selectFirst();
                event.consume();
                break;

            case PAGE_DOWN:
                model.selectLast();
                event.consume();
                break;

            case TAB:
                if (event.isShiftDown()) {
                    model.selectPrevious();
                } else {
                    model.selectNext();
                }

                event.consume();
                break;

            case ENTER:
                if (event.isShiftDown()) {
                    model.selectAboveCell();
                } else {
                    model.selectBelowCell();
                }

                event.consume();
    }

    private void onTableMouseClickEvent(MouseEvent event) {
        if (event.getClickCount() >= 2) {
            onInsertItemAction();
        }
    }

    /**
     * 
     * @return
     */
    private ObservableList<Item> loadFromFile(File file, boolean silent) {
        try (JSONReader json = new JSONReader(new FileReader(file))) {
            ObservableList<Item> list = FXCollections.observableArrayList();

            for (JSONType object : json.readObject().getAsArray()) {
                JSONObject data = object.getAsObject();

                String itemName = data.get("item").getAsString();
                long itemAmount = data.get("amount").getAsNumber();

                list.add(new Item((int) itemAmount, itemName));
            }

            return list;
        } catch (Exception e) {
            if (!silent) {
                Alert alert = new Alert(AlertType.ERROR);

                alert.setTitle("Cannot load file");
                alert.setContentText(e.getMessage());
                alert.setHeaderText(null);

                alert.show();
            }
        }

        return items == null ? FXCollections.observableArrayList() : null;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    private void saveToFile(File file, boolean silent) {
        try (JSONWriter json = new JSONWriter(new FileWriter(file))) {
            JSONArray array = new JSONArray();

            for (Item item : items) {
                JSONObject object = new JSONObject();

                object.putString("item", item.getItemName());
                object.putNumber("amount", (long) item.getItemAmount());

                array.addObject(object);
            }

            json.writeArray(array);
        } catch (Exception e) {
            if (!silent) {
                Alert alert = new Alert(AlertType.ERROR);

                alert.setTitle("Cannot save file");
                alert.setContentText(e.getMessage());
                alert.setHeaderText(null);

                alert.show();
            }
        }
    }

    /**
     * Creates initial scene.
     *
     * @return Scene.
     */
    private Scene createFXMLScene() throws Exception {
        return new Scene(FXMLLoader.load(getClass().getResource("gui.fxml")));
    }

    /**
     * @see Application#start(Stage) start
     */
    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Shopping List App");
            stage.setScene(createFXMLScene());
            stage.setOnCloseRequest(e -> onCloseAction(null));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);

            alert.setTitle("Unknown error");
            alert.setContentText(e.getMessage());
            alert.setHeaderText(null);

            alert.show();
        }

        window = stage;
    }
}