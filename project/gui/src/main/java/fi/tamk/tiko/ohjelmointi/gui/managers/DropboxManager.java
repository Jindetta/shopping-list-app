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
     * Stores token rememeber state.
     */
    private static boolean rememberToken = true;

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
     * Saves current access token to a file.
     */
    private void saveTokenFile() {
        try (FileWriter writer = new FileWriter(TOKEN_FILE)) {
            writer.write(accessToken);
        } catch (Exception e) {
            // Token cannot be saven
        }
    }

    /**
     * Overrides default constructor.
     * @throws InterruptedException Exception is thrown if user cancels.
     */
    public DropboxManager() throws InterruptedException {
        try {
            if ((accessToken = processTokenFile()) == null) {
                InputStream secretFile = getClass().getResourceAsStream("api.json");
                DbxWebAuth webAuth = new DbxWebAuth(APP_CONF, DbxAppInfo.Reader.readFully(secretFile));

                String url = webAuth.authorize(DbxWebAuth.newRequestBuilder().withNoRedirect().build());
                String code = showDropboxAuthenticationDialog(url);

                if (code != null) {
                    DbxAuthFinish authFinish = webAuth.finishFromCode(code);
                    accessToken = authFinish.getAccessToken();

                    if (getRememberTokenState()) {
                        saveTokenFile();
                    }
                } else {
                    throw new InterruptedException();
                }
            }

            client = verifyClientAccessToken(accessToken);
        } catch (InterruptedException cancelException) {
            throw cancelException;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Vefifies current access token.
     */
    private static DbxClientV2 verifyClientAccessToken(String token) {
        DbxClientV2 client = new DbxClientV2(APP_CONF, token);

        try {
            if (client.users().getCurrentAccount() != null) {
                return client;
            }
        } catch (Exception e) {
            TOKEN_FILE.delete();

            throw new RuntimeException(e.getMessage());
        }

        return null;
    }

    /**
     * Checks if access token already exists.
     * @return true if access token data is found, otherwise false.
     */
    private static String processTokenFile() {
        String result = null;

        try (FileReader reader = new FileReader(TOKEN_FILE)) {
            StringBuilder data = new StringBuilder();
            int character;

            while ((character = reader.read()) != -1) {
                data.append((char) character);
            }

            result = data.toString();
        } catch (Exception e) {

        }

        return result;
    }

    /**
     * Checks if token file exists.
     * @return true if file exists, otherwise false.
     */
    public static boolean hasTokenFile() {
        return TOKEN_FILE.isFile();
    }

    /**
     * Deletes token file.
     */
    public static void deleteTokenFile() {
        try {
            verifyClientAccessToken(processTokenFile()).auth().tokenRevoke();
        } catch (Exception e) {

        }

        TOKEN_FILE.delete();
    }

    /**
     * Sets remember token.
     * @param value Token value as Boolean.
     */
    public static void setRememberTokenState(boolean value) {
        rememberToken = value;
    }

    /**
     * Gets remember token.
     * @return true if token should be remembered, otherwise false.
     */
    public static boolean getRememberTokenState() {
        return rememberToken;
    }
}