package fi.tamk.tiko.ohjelmointi.gui;

import fi.tamk.tiko.ohjelmointi.json.*;
import fi.tamk.tiko.ohjelmointi.json.map.JSONMapper;
import fi.tamk.tiko.ohjelmointi.gui.managers.*;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.fxml.FXML;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableRow;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
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
     * 
     */
    private static FileChooser.ExtensionFilter FILE_FILTER = new FileChooser.ExtensionFilter("JSON Files", "*.json");

    /**
     * Stores list items.
     */
    private ObservableList<Item> items;

    /**
     * Stores current stage information.
     */
    private Stage window;

    /**
     * Stores save file information.
     */
    private File saveFile;

    /**
     * Stores {@link TableView} selection model.
     */
    TableViewSelectionModel<Item> selection;

    /**
     * Stores FXML control.
     */
    @FXML
    private TableView<Item> tableView;

    /**
     * Stores FXML control.
     */
    @FXML
    private TableColumn<Item, Long> columnAmount;

    /**
     * Stores FXML control.
     */
    @FXML
    private TableColumn<Item, String> columnItem;

    /**
     * 
     */
    @FXML
    private MenuItem saveMenuItem;

    /**
     * 
     */
    @FXML
    private MenuItem loadStateMenuItem;

    /**
     * Stores FXML control.
     */
    @FXML
    private CheckBox toggleEditMode;

    /**
     * Handles FXML event.
     */
    @FXML
    private void onCreateAction() {
        if (hasUnsavedChanges("Create a new list")) {
            tableView.setItems(items = FXCollections.observableArrayList());

            updateSaveMenuItem(false);
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onOpenAction() {
        if (hasUnsavedChanges("Open an existing list")) {
            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Open shopping list file");
            fileChooser.getExtensionFilters().add(FILE_FILTER);
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

            File file = fileChooser.showOpenDialog(window);

            if (file != null) {
                ObservableList<Item> list = loadFromFile(file, false);

                if (list != null) {
                    tableView.setItems(items = list);

                    updateSaveMenuItem(false);
                }
            }
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onSaveAction() {
        saveToFile(saveFile, false);

        updateSaveMenuItem(true);
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onSaveAsAction() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save shopping list data");
        fileChooser.getExtensionFilters().add(FILE_FILTER);
        fileChooser.setInitialDirectory(new File(getFileDirectoryPath(saveFile)));
        fileChooser.setInitialFileName(saveFile.getName());

        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveFile = file;
            onSaveAction();
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onDropboxImportAction() {
        if (hasUnsavedChanges("Import list from DropBox")) {
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
            } catch (InterruptedException e) {
                // User cancelled this action
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "File import from Dropbox failed", "Cannot import save data from Dropbox.");
            }
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onDropboxExportAction() {
        try {
            DropboxManager manager = new DropboxManager();

            new Thread(() -> {
                onSaveAction();
                manager.uploadFile(saveFile);
                Platform.runLater(() -> showAlert(AlertType.CONFIRMATION, "Export to Dropbox", "List was successfully exported."));
            }).start();
        } catch (InterruptedException e) {
            // User cancelled this action
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "File export to Dropbox failed", "Cannot export save data to Dropbox.");
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onLoadStateAction() {
        if (hasUnsavedChanges("Load list from database")) {
            tableView.setItems(items = DatabaseManager.getItems());
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onSaveStateAction() {
        DatabaseManager.addItems(items);
        loadStateMenuItem.setDisable(false);
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onCloseAction() {
        Platform.exit();
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onSelectAllAction() {
        selection.selectAll();
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onDeselectAllAction() {
        selection.clearSelection();
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onClipboardCutAction() {
        ObservableList<Item> selected = onClipboardCopyAction();

        if (selected != null) {
            items.removeAll(selected);
        }
    }

    /**
     * Handles FXML event.
     * @return Copied items as ObservableList or NULL is unsuccessful.
     */
    @FXML
    private ObservableList<Item> onClipboardCopyAction() {
        ObservableList<Item> selected = selection.getSelectedItems();

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

    /**
     * Handles FXML event.
     */
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
                    int index = selection.getSelectedIndex();

                    if (index == -1) {
                        index = items.size();
                    }

                    items.addAll(index, selected);

                    updateSaveMenuItem(false);
                }
            } catch (Exception e) {
                // Clipboard data was corrupt or otherwise invalid
            }
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onInsertItemAction() {
        int selected = selection.getSelectedIndex();
        Item item = new Item();

        if (selected == -1) {
            selected = items.size();
        }

        if (toggleEditMode.isSelected()) {
            if (showInsertItemDialog(item, "Insert new item").isEmpty()) {
                return;
            }
        }

        items.add(selected, item);

        updateSaveMenuItem(false);
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onEditItemAction() {
        tableView.edit(selection.getSelectedIndex(), columnAmount);

        updateSaveMenuItem(false);
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onDeleteItemAction() {
        if (tableView.getEditingCell() == null) {
            items.removeAll(selection.getSelectedItems());

            updateSaveMenuItem(false);
        }
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onShowAppInfoAction() {
        showAlert(AlertType.INFORMATION, "About", "Shopping List Application by Joonas Lauhala.");
    }

    /**
     * Handles FXML event.
     */
    @FXML
    private void onShowDevSiteAction() {
        openResource("https://github.com/Jindetta/shopping-list-app");
    }

    /**
     * 
     */
    @FXML
    private void onEditCommitAction(CellEditEvent<Item, ?> event) {
        final Object value = event.getNewValue();

        if (value != null) {
            Item selected = selection.getSelectedItem();

            if (value instanceof Long) {
                selected.setItemAmount((Long) value);
            } else {
                selected.setItemName((String) value);
            }

            updateSaveMenuItem(false);
        } else {
            tableView.refresh();
        }
    }

    /**
     * Initializes FXML controls.
     */
    @FXML
    public void initialize() {
        final double DYNAMIC_COLUMN_SIZE = columnAmount.getWidth() + 15;

        columnAmount.setCellValueFactory(new PropertyValueFactory<>("itemAmount"));
        columnAmount.setCellFactory(TextFieldTableCell.forTableColumn(new CustomLongConverter()));

        columnItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        columnItem.prefWidthProperty().bind(tableView.widthProperty().subtract(DYNAMIC_COLUMN_SIZE));
        columnItem.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.setRowFactory(this::onRowClickCallbackEvent);
        tableView.setOnKeyPressed(this::onTableKeyPressEvent);

        saveFile = new File("list.json");
        selection = tableView.getSelectionModel();
        tableView.setItems(items = loadFromFile(saveFile, true));
        selection.setSelectionMode(SelectionMode.MULTIPLE);

        updateSaveMenuItem(!items.isEmpty());
    }

    /**
     * 
     * @param table
     * @return
     */
    private TableRow<Item> onRowClickCallbackEvent(TableView<Item> table) {
        final TableRow<Item> row = new TableRow<>();

        row.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && row.getIndex() >= items.size()) {
                selection.clearSelection();
                table.edit(-1, null);

                if (event.getClickCount() >= 2) {
                    onInsertItemAction();
                }

                event.consume();
            }
        });

        return row;
    }

    /**
     * Handles key press events.
     * @param event KeyEvent.
     */
    private void onTableKeyPressEvent(KeyEvent event) {
        switch (event.getCode()) {
            case PAGE_UP:
                selection.selectFirst();
                break;

            case PAGE_DOWN:
                selection.selectLast();
                break;

            default:
                return;
        }

        event.consume();
    }

    /**
     * Load list data from file.
     * @return Loaded data as {@link ObservableList} or NULL if unsuccessful.
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
                showAlert(AlertType.ERROR, "Cannot load file", "Unable to read save data.");
            }
        }

        return items == null ? FXCollections.observableArrayList() : null;
    }

    /**
     * Saves list data to file.
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
                showAlert(AlertType.ERROR, "Cannot save file", "Unable to write save data.");
            }
        }
    }

    /**
     * 
     * @param disableSave
     */
    private void updateSaveMenuItem(boolean disableSave) {
        saveMenuItem.setDisable(disableSave);
    }

    /**
     * 
     * @param message
     * @return
     */
    private boolean hasUnsavedChanges(String message) {
        if (!saveMenuItem.isDisable()) {
            return showConfirmDialog(AlertType.CONFIRMATION, message, "Unsaved changes will be lost.\nAre you sure?");
        }

        return true;
    }

    /**
     * Creates initial scene.
     * @return Scene.
     */
    private Scene createFXMLScene() throws Exception {
        return new Scene(FXMLLoader.load(getClass().getResource("gui.fxml")));
    }

    /**
     * 
     */
    @Override
    public void init() {
        new Thread(DatabaseManager::initialize).start();
    }

    /**
     * @see Application#start(Stage) start
     */
    @Override
    public void start(Stage stage) {
        try {
            window = stage;

            stage.setTitle("Shopping List App");
            stage.setOnCloseRequest(e -> onCloseAction());
            stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
            stage.setScene(createFXMLScene());
            stage.centerOnScreen();

            stage.showingProperty().addListener((observable, oldValue, showing) -> {
                if(showing) {
                    stage.setMinHeight(stage.getHeight());
                    stage.setMinWidth(stage.getWidth());
                }
            });

            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Unknown error", "Application initialization failed.");
        }
    }

    /**
     * 
     */
    @Override
    public void stop() {
        DatabaseManager.destroy();
    }

    /**
     * 
     * @param path
     * @return
     */
    private static String getFileDirectoryPath(File path) {
        String fullPath = path.getAbsolutePath();

        if (path.isFile()) {
            return fullPath.substring(0, fullPath.lastIndexOf(path.getName()));
        }

        return fullPath;
    }

    /**
     * Shows alert dialog.
     * @param type    Dialog type.
     * @param title   Dialog title.
     * @param message Dialog message content.
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
     * @return
     */
    private static Optional<Item> showInsertItemDialog(Item item, String title) {
        final Dialog<Item> dialog = new Dialog<>();
        final DialogPane panel = dialog.getDialogPane();

        dialog.setTitle(title);
        dialog.initStyle(StageStyle.UTILITY);

        TextField itemAmount = new TextField(item.getItemAmount().toString());
        TextField itemName = new TextField(item.getItemName());

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(new Label("Amount:"), 0, 0);
        gridPane.add(itemAmount, 1, 0);
        gridPane.add(new Label("Item:"), 0, 1);
        gridPane.add(itemName, 1, 1);

        panel.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        panel.setContent(gridPane);

        Platform.runLater(() -> itemAmount.requestFocus());

        itemAmount.addEventFilter(KeyEvent.ANY, e -> {
            panel.lookupButton(ButtonType.OK).setDisable(!itemAmount.getText().matches("^-?\\d+$"));
        });

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                item.setItemName(itemName.getText());
                item.setItemAmount(Long.parseLong(itemAmount.getText()));

                return item;
            }

            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Shows confirm dialog.
     * @param type    Dialog type.
     * @param title   Dialog title.
     * @param message Dialog message content.
     */
    private static boolean showConfirmDialog(AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.YES, ButtonType.NO);

        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setTitle(title);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    /**
     * Shows text input dialog.
     * @param title   Dialog title.
     * @param message Dialog message content.
     * @return User entered String or NULL if cancelled.
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
     * Opens URL resource.
     * @param url URL address.
     */
    public static void openResource(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            // Possibly invalid URL?
        }
    }
}