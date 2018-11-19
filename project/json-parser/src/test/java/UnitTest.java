import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;

public class UnitTest {

    /**
     *
     */
    @Test
    public void testJSONArray() {
        System.out.println("--[[ Tokenize JSONArray ]]--");
        final String JSON = "['item1', 'item2', 'item3']";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON);
        JSONType object = tokenizer.parse();

        Assertions.assertTrue(object instanceof JSONType);
        Assertions.assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        Assertions.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String VALUE = String.format("item%d", i + 1);

            Assertions.assertEquals(VALUE, array.get(i).getAsString());
            System.out.printf("Success: \"%s\" was found at [%d]%n", VALUE, i);
        }

        Assertions.assertNull(tokenizer.parse());
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

        Assertions.assertTrue(object instanceof JSONType);
        Assertions.assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        Assertions.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String KEY = String.format("key%d", i + 1);
            final String VALUE = String.format("item%d", i + 1);

            Assertions.assertTrue(array.containsKey(KEY));
            Assertions.assertEquals(VALUE, array.get(KEY).getAsString());
            System.out.printf("Success: \"%s\" was found at [\"%s\"]%n", VALUE, KEY);
        }

        Assertions.assertNull(tokenizer.parse());
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

        Assertions.assertTrue(object instanceof JSONType);
        Assertions.assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        Assertions.assertEquals(2, array.size());

        Assertions.assertEquals(json.get("key"), array.get("key"));
        Assertions.assertEquals(json.get("number"), array.get("number"));

        Assertions.assertNull(tokenizer.parse());

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

        Assertions.assertTrue(object instanceof JSONType);
        Assertions.assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        Assertions.assertEquals(2, array.size());

        for (int i = 0; i < array.size(); i++) {
            Assertions.assertEquals(json.get(i), array.get(i));
        }

        Assertions.assertNull(tokenizer.parse());

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
        Assertions.assertEquals(JSONType.createNull(), tokenizer.parse());
        System.out.println("Success: Null object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("44332211");
        Assertions.assertEquals(JSONType.createNumber(44332211l), tokenizer.parse());
        System.out.println("Success: Long object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("1.23456789");
        Assertions.assertEquals(JSONType.createDecimal(1.23456789), tokenizer.parse());
        System.out.println("Success: Double object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("\"String_value!\\n\"");
        Assertions.assertEquals(JSONType.createString("String_value!\n"), tokenizer.parse());
        System.out.println("Success: String object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("[]");
        Assertions.assertEquals(JSONType.createArray(new JSONArray()), tokenizer.parse());
        System.out.println("Success: Array object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("{}");
        Assertions.assertEquals(JSONType.createObject(new JSONObject()), tokenizer.parse());
        System.out.println("Success: Object object");
        Assertions.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("true");
        Assertions.assertEquals(JSONType.createBoolean(true), tokenizer.parse());
        System.out.println("Success: Boolean object");
        Assertions.assertNull(tokenizer.parse());
    }

    /**
     * 
     */
    @Test
    public void testWriteString() {
        System.out.println("--[[ Write String ]]--");
        final String STRING_VALUE = "\"{Word\\n\\\"P/e\\\\r\\\"\\nLine!\\n\"";

        Assertions.assertEquals(STRING_VALUE, JSONTokenizer.writeString("{Word\n\"P/e\\r\"\nLine!\n"));
        System.out.println("Success: Valid JSON string");
    }

    /**
     * 
     */
    @Test
    public void testJSONExceptions() {
        System.out.println("--[[ Exceptions ]]--");

        Assertions.assertThrows(JSONException.class, () -> new JSONTokenizer("0 0").parse());
        Assertions.assertThrows(JSONException.class, () -> new JSONTokenizer("'string',").parse());
        Assertions.assertThrows(JSONException.class, () -> new JSONTokenizer("'stri\ng'").parse());
        Assertions.assertThrows(JSONException.class, () -> new JSONTokenizer("{\"key\":}").parse());
        Assertions.assertThrows(JSONException.class, () -> new JSONTokenizer("\t\n\r [null, 0,,1]").parse());

        System.out.println("Success: All exceptions thrown");
    }
}