import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;
import fi.tamk.tiko.ohjelmointi.json.map.*;

/**
 * Tests for object equality.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONEqualityTest extends Assertions {

    /**
     * Tests JSON mapping from data.
     */
    @Test
    public void testJSONMapper() {
        System.out.println("--[[ JSONMapper ]]--");
        final String PERSON_NAME = "Marja-Liisa";

        JSONObject personData = new JSONObject();
        personData.putString("name", PERSON_NAME);

        personData = JSONMapper.mapObject(Person.class, personData);
        Person person = JSONMapper.loadInstanceMapping(new Person(), personData);

        assertTrue(person instanceof Person);
        assertSame(PERSON_NAME, person.getName());

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests JSON tokenizer to array,
     */
    @Test
    public void testJSONArrayTokenizer() {
        System.out.println("--[[ Tokenize JSONArray ]]--");
        final String JSON = "['item1', 'item2', 'item3']";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON);
        JSONType object = tokenizer.parse();

        assertTrue(object instanceof JSONType);
        assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        assertEquals(3, array.size());
        assertNull(tokenizer.parse());

        for (int i = 0; i < array.size(); i++) {
            final String VALUE = String.format("item%d", i + 1);

            assertEquals(VALUE, array.get(i).getAsString());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests JSON tokenizer to object.
     */
    @Test
    public void testJSONObjectTokenizer() {
        System.out.println("--[[ Tokenize JSONObject ]]--");
        final String JSON = "{'key1': 'item1', 'key2': 'item2', 'key3': 'item3'}";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON);
        JSONType object = tokenizer.parse();

        assertTrue(object instanceof JSONType);
        assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        assertEquals(3, array.size());
        assertNull(tokenizer.parse());

        for (int i = 0; i < array.size(); i++) {
            final String KEY = String.format("key%d", i + 1);
            final String VALUE = String.format("item%d", i + 1);

            assertTrue(array.containsKey(KEY));
            assertEquals(VALUE, array.get(KEY).getAsString());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests JSON object tokenization.
     */
    @Test
    public void testJSONObjectEquality() {
        System.out.println("--[[ Write JSONObject ]]--");

        JSONObject json = new JSONObject();
        json.put("key", JSONType.createString("value"));
        json.put("number", JSONType.createNumber(10l));

        JSONTokenizer tokenizer = new JSONTokenizer(JSONTokenizer.writeObject(json));
        JSONType object = tokenizer.parse();

        assertTrue(object instanceof JSONType);
        assertEquals(JSONTypes.OBJECT, object.getType());
        assertEquals(json, object.getAsObject());
        assertNull(tokenizer.parse());

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests JSON array tokenization.
     */
    @Test
    public void testJSONArrayEquality() {
        System.out.println("--[[ Write JSONArray ]]--");

        JSONArray json = new JSONArray();
        json.add(JSONType.createString("value"));
        json.add(JSONType.createNumber(10l));

        JSONTokenizer tokenizer = new JSONTokenizer(JSONTokenizer.writeArray(json));
        JSONType object = tokenizer.parse();

        assertTrue(object instanceof JSONType);
        assertEquals(JSONTypes.ARRAY, object.getType());
        assertEquals(json, object.getAsArray());
        assertNull(tokenizer.parse());

        System.out.println("Success: All tests completed");
    }

    /**
     * Helper class for JSON mapper test.
     */
    @JSONMappable
    @SuppressWarnings("all")
    private class Person {

        /**
         * Stores person name.
         */
        @JSONData(key="name")
        private String name;

        /**
         * Sets person name.
         * @param name Person name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets person name.
         * @return Person name as String.
         */
        public String getName() {
            return name;
        }
    }
}