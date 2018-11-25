package fi.tamk.tiko.ohjelmointi.gui;

import java.util.Optional;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
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

    private String accessToken;

    private DbxClientV2 client;

    private static final String APP_NAME = "ShoppingListApp";

    /**
     * 
     */
    private static final String TOKEN_FILE = "userToken.dat";

    public void uploadFile(File file) {
        try (InputStream input = new FileInputStream(file)) {
            client.files().uploadBuilder("/list.json").withMode(WriteMode.OVERWRITE).uploadAndFinish(input);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void downloadFile(File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            client.files().downloadBuilder("/list.json").download(output);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean hasTokenFile() {
        try (FileReader reader = new FileReader(TOKEN_FILE)) {
            StringBuilder data = new StringBuilder();
            int character;

            while ((character = reader.read()) != -1) {
                data.append((char) character);
            }

            accessToken = data.toString();
        } catch (Exception e) {
            
        }

        return accessToken != null;
    }

    private void saveTokenFile() {
        try (FileWriter writer = new FileWriter(TOKEN_FILE)) {
            writer.write(accessToken);
        } catch (Exception e) {

        }
    }

    private void verifyClient(DbxRequestConfig config) throws Exception {
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        if (client.users().getCurrentAccount() != null) {
            this.client = client;
        }
    }

    public DropboxManager() {
        try {
            DbxRequestConfig config = new DbxRequestConfig(APP_NAME);

            if (!hasTokenFile()) {
                InputStream key = getClass().getResourceAsStream("apitoken.json");
                DbxAppInfo appInfo = DbxAppInfo.Reader.readFully(key);

                DbxWebAuth webAuth = new DbxWebAuth(config, appInfo);
                DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();

                Desktop desktop = java.awt.Desktop.getDesktop();
                desktop.browse(new URI(webAuth.authorize(webAuthRequest)));
    
                TextInputDialog tokenInput = new TextInputDialog();
    
                tokenInput.setTitle("Dropbox Authentication");
                tokenInput.setContentText("Token:");
                tokenInput.setHeaderText(null);
    
                Optional<String> code = tokenInput.showAndWait();
    
                if (code.isPresent()) {
                    DbxAuthFinish authFinish = webAuth.finishFromCode(code.get());
                    accessToken = authFinish.getAccessToken();

                    saveTokenFile();
                }
            }

            verifyClient(config);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}