import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;

public class JSONParseTest extends Assertions {

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

        for (int i = 0; i < array.size(); i++) {
            final String VALUE = String.format("item%d", i + 1);

            assertEquals(VALUE, array.get(i).getAsString());
            System.out.printf("Success: \"%s\" was found at [%d]%n", VALUE, i);
        }

        assertNull(tokenizer.parse());
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

        for (int i = 0; i < array.size(); i++) {
            final String KEY = String.format("key%d", i + 1);
            final String VALUE = String.format("item%d", i + 1);

            assertTrue(array.containsKey(KEY));
            assertEquals(VALUE, array.get(KEY).getAsString());
            System.out.printf("Success: \"%s\" was found at [\"%s\"]%n", VALUE, KEY);
        }

        assertNull(tokenizer.parse());
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

        JSONObject array = object.getAsObject();
        assertEquals(2, array.size());

        assertEquals(json.get("key"), array.get("key"));
        assertEquals(json.get("number"), array.get("number"));

        assertNull(tokenizer.parse());

        System.out.println("Success: Valid JSON information");
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

        JSONArray array = object.getAsArray();
        assertEquals(2, array.size());

        for (int i = 0; i < array.size(); i++) {
            assertEquals(json.get(i), array.get(i));
        }

        assertNull(tokenizer.parse());

        System.out.println("Success: Valid JSON information");
    }

    /**
     *
     */
    @Test
    public void testSingleUnits() {
        System.out.println("--[[ Single Units ]]--");
        JSONTokenizer tokenizer;

        tokenizer = new JSONTokenizer("null");
        assertEquals(JSONType.createNull(), tokenizer.parse());
        System.out.println("Success: Null object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("44332211");
        assertEquals(JSONType.createNumber(44332211l), tokenizer.parse());
        System.out.println("Success: Long object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("1.23456789");
        assertEquals(JSONType.createDecimal(1.23456789), tokenizer.parse());
        System.out.println("Success: Double object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("\"String_value!\\n\"");
        assertEquals(JSONType.createString("String_value!\n"), tokenizer.parse());
        System.out.println("Success: String object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("[]");
        assertEquals(JSONType.createArray(new JSONArray()), tokenizer.parse());
        System.out.println("Success: Array object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("{}");
        assertEquals(JSONType.createObject(new JSONObject()), tokenizer.parse());
        System.out.println("Success: Object object");
        assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("true");
        assertEquals(JSONType.createBoolean(true), tokenizer.parse());
        System.out.println("Success: Boolean object");
        assertNull(tokenizer.parse());
    }

    /**
     * 
     */
    @Test
    public void testWriteString() {
        System.out.println("--[[ Write String ]]--");
        final String STRING_VALUE = "\"{Word\\n\\\"P/e\\\\r\\\"\\nLine!\\n\"";

        assertEquals(STRING_VALUE, JSONTokenizer.writeString("{Word\n\"P/e\\r\"\nLine!\n"));
        System.out.println("Success: Valid JSON string");
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

        System.out.println("Success: All exceptions thrown");
    }
}