package fi.tamk.tiko.ohjelmointi.gui;

import fi.tamk.tiko.ohjelmointi.json.*;
import fi.tamk.tiko.ohjelmointi.json.map.JSONMapper;

import javafx.util.converter.LongStringConverter;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;

import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Optional;

import java.net.URI;

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
    private TableColumn<Item, Long> columnAmount;

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
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

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
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setInitialFileName(saveFile.getName());

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

            new Thread(() -> {
                manager.downloadFile(saveFile);
                ObservableList<Item> list = loadFromFile(saveFile, false);

                if (list != null) {
                    tableView.setItems(items = list);
                    Platform.runLater(() -> showAlert(AlertType.CONFIRMATION, "Import from Dropbox", "List was successfully imported."));
                }
            }).start();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "File import from Dropbox failed", e.getMessage());
        }
    }

    @FXML
    private void onDropboxExportAction() {
        try {
            DropboxManager manager = new DropboxManager();

            new Thread(() -> {
                manager.uploadFile(saveFile);
                Platform.runLater(() -> showAlert(AlertType.CONFIRMATION, "Export to Dropbox", "List was successfully exported."));
            }).start();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "File export to Dropbox failed", e.getMessage());
        }
    }

    @FXML
    private void onCloseAction() {
        Platform.exit();
    }

    @FXML
    private void onUndoAction() {

    }

    @FXML
    private void onRedoAction() {

    }

    @FXML
    private void onClipboardCutAction() {
        ObservableList<Item> selected = onClipboardCopyAction();

        if (selected != null) {
            items.removeAll(selected);
        }
    }

    @FXML
    private ObservableList<Item> onClipboardCopyAction() {
        ObservableList<Item> selected = tableView.getSelectionModel().getSelectedItems();

        if (selected != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            JSONArray array = new JSONArray();

            for (Item item : selected) {
                array.addObject(JSONMapper.saveMapping(item));
            }

            content.putString(JSONType.getJSONString(array));
            clipboard.setContent(content);
        }

        return selected;
    }

    @FXML
    private void onClipboardPasteAction() {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            try {
                ObservableList<Item> selected = FXCollections.observableArrayList();

                for (JSONType type : new JSONTokenizer(clipboard.getString()).parse().getAsArray()) {
                    selected.add(JSONMapper.loadClassMapping(Item.class, type.getAsObject()));
                }

                if (!selected.isEmpty()) {
                    int index = tableView.getSelectionModel().getSelectedIndex();

                    if (index == -1) {
                        index = items.size();
                    }

                    items.addAll(index, selected);
                }
            } catch (Exception e) {
                // Clipboard data was corrupt or otherwise invalid
            }
        }
    }

    @FXML
    private void onInsertItemAction() {
        int selected = tableView.getSelectionModel().getSelectedIndex();

        if (selected == -1) {
            selected = items.size();
        }

        items.add(selected, new Item());

        tableView.layout();
        tableView.edit(selected, columnAmount);
    }

    @FXML
    private void onDeleteItemAction() {
        items.removeAll(tableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void onShowHelpAction() {
        showAlert(AlertType.INFORMATION, "About", "Shopping List Application by Joonas Lauhala.");
    }

    @FXML
    public void initialize() {
        saveFile = new File("list.json");
        items = loadFromFile(saveFile, true);

        columnItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        columnItem.prefWidthProperty().bind(tableView.widthProperty().subtract(102));
        columnItem.setCellFactory(TextFieldTableCell.forTableColumn());

        columnAmount.setCellValueFactory(new PropertyValueFactory<>("itemAmount"));
        columnAmount.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);        
        tableView.setOnKeyPressed(this::onTableKeyPressEvent);

        tableView.setEditable(true);
        tableView.setItems(items);
    }

    private void onTableKeyPressEvent(KeyEvent event) {
        TableViewSelectionModel<Item> model = tableView.getSelectionModel();

        switch (event.getCode()) {
            case INSERT:
                onInsertItemAction();
                break;

            case DELETE:
                onDeleteItemAction();
                break;

            case PAGE_UP:
                model.selectFirst();
                break;

            case PAGE_DOWN:
                model.selectLast();
                break;

            case TAB:
                if (event.isShiftDown()) {
                    if (model.isSelected(0)) {
                        model.selectLast();
                    } else {
                        model.selectPrevious();
                    }
                } else {
                    if (model.isSelected(items.size() - 1)) {
                        model.selectFirst();
                    } else {
                        model.selectNext();
                    }
                }

                break;

            case A:
                if (event.isControlDown()) {
                    model.selectAll();
                    break;
                }

            default:
                return;
        }

        event.consume();
    }

    /**
     * 
     * @return
     */
    private ObservableList<Item> loadFromFile(File file, boolean silent) {
        try (JSONReader json = new JSONReader(new FileReader(file))) {
            ObservableList<Item> list = FXCollections.observableArrayList();

            for (JSONType object : json.readObject().getAsArray()) {
                list.add(JSONMapper.loadClassMapping(Item.class, object.getAsObject()));
            }

            return list;
        } catch (Exception e) {
            if (!silent) {
                showAlert(AlertType.ERROR, "Cannot load file", e.getMessage());
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
                array.addObject(JSONMapper.saveMapping(item));
            }

            json.writeArray(array);
        } catch (Exception e) {
            if (!silent) {
                showAlert(AlertType.ERROR, "Cannot save file", e.getMessage());
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
            stage.setOnCloseRequest(e -> onCloseAction());
            stage.setScene(createFXMLScene());
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Unknown error", e.getMessage());
        }

        window = stage;
    }

    /**
     * 
     * @param type
     * @param title
     * @param message
     */
    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);

        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setTitle(title);

        alert.show();
    }

    /**
     * 
     * @param title
     * @param message
     * @return
     */
    public static String showTextInput(String title, String message) {
        TextInputDialog input = new TextInputDialog();

        input.setContentText(message);
        input.setHeaderText(null);
        input.setTitle(title);

        Optional<String> result = input.showAndWait();
        return result.isPresent() ? result.get() : null;
    }

    /**
     * 
     * @param url
     */
    public static void openResource(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            // Possibly invalid URL?
        }
    }
}