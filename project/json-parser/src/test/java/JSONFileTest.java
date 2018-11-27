import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileOutputStream;

public class JSONFileTest extends Assertions {

    /**
     * Replicates person info data from JSON.
     *
     * @return JSONObject.
     */
    private JSONObject createPersonInformation() {
        JSONObject personInfo = new JSONObject();

        personInfo.put("firstName", JSONType.createString("John"));
        personInfo.put("lastName", JSONType.createString("Smith"));
        personInfo.put("isAlive", JSONType.createBoolean(true));
        personInfo.put("age", JSONType.createNumber(27L));

        JSONObject addressInfo = new JSONObject();

        addressInfo.put("streetAddress", JSONType.createString("21 2nd Street"));
        addressInfo.put("city", JSONType.createString("New York"));
        addressInfo.put("state", JSONType.createString("NY"));
        addressInfo.put("postalCode", JSONType.createString("10021-3100"));

        personInfo.put("address", JSONType.createObject(addressInfo));

        JSONArray phoneInfo = new JSONArray();

        JSONObject homePhone = new JSONObject();
        homePhone.put("type", JSONType.createString("home"));
        homePhone.put("number", JSONType.createString("212 555-1234"));

        JSONObject officePhone = new JSONObject();
        officePhone.put("type", JSONType.createString("office"));
        officePhone.put("number", JSONType.createString("646 555-4567"));
        
        JSONObject mobilePhone = new JSONObject();
        mobilePhone.put("type", JSONType.createString("mobile"));
        mobilePhone.put("number", JSONType.createString("123 456-7890"));

        phoneInfo.add(JSONType.createObject(homePhone));
        phoneInfo.add(JSONType.createObject(officePhone));
        phoneInfo.add(JSONType.createObject(mobilePhone));

        personInfo.put("phoneNumbers", JSONType.createArray(phoneInfo));
        personInfo.put("children", JSONType.createArray(new JSONArray()));
        personInfo.put("spouse", JSONType.createNull());

        return personInfo;
    }

    /**
     * Test JSON file reading.
     */
    @Test
    public void testJSONReader() {
        System.out.println("--[[ JSON Reader ]]--");
        String path = getClass().getResource("numberArray.json").getPath();
        JSONType value = null;

        try (JSONReader reader = new JSONReader(new FileReader(path))) {
            value = reader.readObject();

            assertTrue(value instanceof JSONType);
            assertNull(reader.readObject());
        } catch (Exception e) {
            fail("Cannot read from resource: " + path);
        }

        final int SIZE = 10;
        JSONArray array = value.getAsArray();
        assertEquals(SIZE, array.size());

        for (int i = 0; i < SIZE; i++) {
            assertEquals(Long.valueOf(i), array.get(i).getAsNumber());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Test JSON file reading from stream.
     */
    @Test
    public void testJSONReaderStream() {
        System.out.println("--[[ JSON Reader from Stream ]]--");
        InputStream stream = getClass().getResourceAsStream("personInfo.json");
        JSONType value = null;

        try (JSONReader reader = new JSONReader(stream)) {
            value = reader.readObject();

            assertTrue(value instanceof JSONType);
            assertNull(reader.readObject());
        } catch (Exception e) {
            fail("Cannot load resource from stream");
        }

        assertEquals(createPersonInformation(), value.getAsObject());
        System.out.println("Success: All tests completed");
    }

    /**
     * Test JSON file writing.
     */
    @Test
    public void testJSONWriter() {
        System.out.println("--[[ JSON Writer ]]--");
        String path = getClass().getResource("").getPath();
        String file = path + "savedNumberArray.json";

        JSONArray array = new JSONArray();
        final int SIZE = 10;

        for (int i = 0; i < SIZE; i++) {
            array.add(JSONType.createNumber((long) i));
        }

        assertEquals(SIZE, array.size());

        try (JSONWriter writer = new JSONWriter(new FileWriter(file))) {
            writer.writeArray(array);
        } catch (Exception e) {
            fail("Cannot write to resource: " + file);
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Test JSON file writing to stream.
     */
    @Test
    public void testJSONWriterStream() {
        System.out.println("--[[ JSON Writer to Stream ]]--");
        String path = getClass().getResource("").getPath();
        String file = path + "savedPersonInfo.json";

        try (JSONWriter writer = new JSONWriter(new FileOutputStream(file))) {
            writer.writeObject(createPersonInformation());
        } catch (Exception e) {
            fail("Cannot save resource as stream");
        }

        System.out.println("Success: All tests completed");
    }
}