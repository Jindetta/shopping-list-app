package fi.tamk.tiko.ohjelmointi.gui.managers;

import static fi.tamk.tiko.ohjelmointi.gui.GUI.*;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.files.WriteMode;

/**
 * Manages Dropbox integration with application.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class DropboxManager {

    /**
     * Stores an user authentication token.
     */
    private static final File TOKEN_FILE = new File("userToken.dat");

    /**
     * Stores Dropbox application configuration data.
     */
    private static final DbxRequestConfig APP_CONF = new DbxRequestConfig("ShoppingListApp");

    /**
     * Stores current access token.
     */
    private String accessToken;

    /**
     * Stores Dropbox client data.
     */
    private DbxClientV2 client;

    /**
     * Uploads a file to Dropbox application directory.
     * @param file {@link File} to upload.
     */
    public void uploadFile(File file) {
        try (FileInputStream input = new FileInputStream(file)) {
            client.files().uploadBuilder("/list.json").withMode(WriteMode.OVERWRITE).uploadAndFinish(input);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Downloads a file from Dropbox application directory.
     * @param file {@link File} to replace.
     */
    public void downloadFile(File file) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            client.files().downloadBuilder("/list.json").download(output);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Checks if access token already exists.
     * @return true if access token data is found, otherwise false.
     */
    private boolean hasTokenFile() {
        try (FileReader reader = new FileReader(TOKEN_FILE)) {
            StringBuilder data = new StringBuilder();
            int character;

            while ((character = reader.read()) != -1) {
                data.append((char) character);
            }

            accessToken = data.toString();
        } catch (Exception e) {
            // No readable token file present
        }

        return accessToken != null;
    }

    /**
     * Saves current access token to a file.
     */
    private void saveTokenFile() {
        try (FileWriter writer = new FileWriter(TOKEN_FILE)) {
            writer.write(accessToken);
        } catch (Exception e) {

        }
    }

    /**
     * Vefifies current access token.
     */
    private void verifyClientAccessToken() {
        DbxClientV2 client = new DbxClientV2(APP_CONF, accessToken);

        try {
            if (client.users().getCurrentAccount() != null) {
                this.client = client;
            }
        } catch (Exception e) {
            TOKEN_FILE.delete();

            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Overrides default constructor.
     */
    public DropboxManager() {
        try {
            if (!hasTokenFile()) {
                InputStream secretFile = getClass().getResourceAsStream("../api.json");
                DbxWebAuth webAuth = new DbxWebAuth(APP_CONF, DbxAppInfo.Reader.readFully(secretFile));
                DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();

                openResource(webAuth.authorize(webAuthRequest));
                String code = showTextInput("Dropbox Authentication", "Token:");

                if (code != null) {
                    DbxAuthFinish authFinish = webAuth.finishFromCode(code);
                    accessToken = authFinish.getAccessToken();

                    saveTokenFile();
                }
            }

            if (accessToken != null) {
                verifyClientAccessToken();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}