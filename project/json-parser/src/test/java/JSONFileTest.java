import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.io.FileReader;
import java.io.FileWriter;

public class JSONFileTest extends Assertions {

    /**
     * 
     */
    @Test
    public void testJSONReader() {
        System.out.println("--[[ JSON Reader ]]--");
        ClassLoader loader = getClass().getClassLoader();
        String path = loader.getResource("numberArray.json").getPath();
        JSONType value = null;

        try (JSONReader reader = new JSONReader(new FileReader(path))) {
            value = reader.readNext();

            assertTrue(value instanceof JSONType);
            assertNull(reader.readNext());
        } catch (Exception e) {
            fail("Cannot read resource: " + path);
        }

        final int SIZE = 10;
        JSONArray array = value.getAsArray();
        assertEquals(SIZE, array.size());

        for (int i = 0; i < SIZE; i++) {
            assertEquals(Long.valueOf(i), array.get(i).getAsNumber());
            System.out.printf("Success: Index (%d) was a match%n", i);
        }
    }

    /**
     * 
     */
    @Test
    public void testJSONWriter() {
        System.out.println("--[[ JSON Writer ]]--");
        ClassLoader loader = getClass().getClassLoader();
        String path = loader.getResource("savedNumberArray.json").getPath();

        JSONArray array = new JSONArray();
        final int SIZE = 10;

        for (int i = 0; i < SIZE; i++) {
            array.add(JSONType.createNumber((long) i));
        }

        assertEquals(SIZE, array.size());

        try (JSONWriter writer = new JSONWriter(new FileWriter(path))) {
            writer.writeArray(array);
        } catch (Exception e) {
            fail("Cannot write to resource: " + path);
        }

        System.out.println("Success: savedNumberArray.json was saved");
    }
}