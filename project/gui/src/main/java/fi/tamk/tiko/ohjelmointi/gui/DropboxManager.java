package fi.tamk.tiko.ohjelmointi.gui;

import java.util.Optional;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import javafx.scene.control.TextInputDialog;

/**
 * 
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class DropboxManager {

    /**
     * 
     */
    public static String getAccessToken() {
        String accessToken = null;
        DbxAppInfo appInfo;

        try {
            String file = DropboxManager.class.getResource("apitoken.json").getPath();
            appInfo = DbxAppInfo.Reader.readFromFile(file);

            DbxWebAuth webAuth = new DbxWebAuth(new DbxRequestConfig("examples-authorize"), appInfo);
            DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();

            Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.browse(new URI(webAuth.authorize(webAuthRequest)));

            TextInputDialog tokenInput = new TextInputDialog();

            tokenInput.setTitle("Dropbox Authentication");
            tokenInput.setContentText("Authentication token:");
            tokenInput.setHeaderText(null);

            Optional<String> code = tokenInput.showAndWait();

            if (code.isPresent()) {
                DbxAuthFinish authFinish = webAuth.finishFromCode(code.get());
                accessToken = authFinish.getAccessToken();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return accessToken;
    }

    public static DbxClientV2 getClient(String token) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("ShoppingListApp").build();

        return new DbxClientV2(config, token);
    }

    public static void uploadFile(DbxClientV2 client, File file) {
        try (InputStream input = new FileInputStream(file)) {
            client.files().uploadBuilder("/list.json").withMode(WriteMode.OVERWRITE).uploadAndFinish(input);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void downloadFile(DbxClientV2 client, File file) {
        try {
            FileOutputStream output = new FileOutputStream(file);

            client.files().downloadBuilder("/list.json").download(output);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}