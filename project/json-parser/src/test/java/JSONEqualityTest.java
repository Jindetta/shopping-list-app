import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;
import fi.tamk.tiko.ohjelmointi.json.map.*;

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

        Person person = JSONMapper.map(new Person(), personData);

        assertTrue(person instanceof Person);
        assertSame(PERSON_NAME, person.name);

        System.out.println("Success: All tests completed");
    }

    /**
     *
     */
    @Test
    public void testJSONArray() {
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
     *
     */
    @Test
    public void testJSONObject() {
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
     *
     */
    @Test
    public void testJSONObjectWrite() {
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
     *
     */
    @Test
    public void testJSONArrayWrite() {
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
     * 
     */
    @Test
    public void testJSONExceptions() {
        System.out.println("--[[ Exceptions ]]--");

        assertThrows(JSONException.class, () -> new JSONTokenizer("0 0").parse());
        assertThrows(JSONException.class, () -> new JSONTokenizer("'string',").parse());
        assertThrows(JSONException.class, () -> new JSONTokenizer("'stri\ng'").parse());
        assertThrows(JSONException.class, () -> new JSONTokenizer("{\"key\":}").parse());
        assertThrows(JSONException.class, () -> new JSONTokenizer("\t\n\r [null, 0,,1]").parse());

        System.out.println("Success: All tests completed");
    }

    /**
     * 
     */
    private class Person implements JSONMappable {

        /**
         *
         */
        public String name;

        /**
         *
         */
        @Override
        public boolean isJSONMappable() {
            return true;
        }
    }
}