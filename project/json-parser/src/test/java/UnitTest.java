import org.junit.*;

import fi.tamk.tiko.ohjelmointi.json.*;

public class UnitTest {

    /**
     *
     */
    @Test
    public void testJSONArray() {
        System.out.println("--[[ Tokenize JSONArray ]] --");
        final String JSON = "['item1', 'item2', 'item3']";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON);
        JSONType object = tokenizer.parse();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        Assert.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String VALUE = String.format("item%d", i + 1);

            Assert.assertEquals(VALUE, array.get(i).getAsString());
            System.out.printf("Success: \"%s\" was found at [%d]%n", VALUE, i);
        }

        Assert.assertNull(tokenizer.parse());
    }

    /**
     *
     */
    @Test
    public void testJSONObject() {
        System.out.println("--[[ Tokenize JSONObject ]] --");
        final String JSON = "{'key1': 'item1', 'key2': 'item2', 'key3': 'item3'}";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON);
        JSONType object = tokenizer.parse();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        Assert.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String KEY = String.format("key%d", i + 1);
            final String VALUE = String.format("item%d", i + 1);

            Assert.assertTrue(array.containsKey(KEY));
            Assert.assertEquals(VALUE, array.get(KEY).getAsString());
            System.out.printf("Success: \"%s\" was found at [\"%s\"]%n", VALUE, KEY);
        }

        Assert.assertNull(tokenizer.parse());
    }

    /**
     *
     */
    @Test
    public void testJSONObjectWrite() {
        System.out.println("--[[ Write JSONObject ]] --");

        JSONObject json = new JSONObject();
        json.put("key", JSONType.createString("value"));
        json.put("number", JSONType.createNumber(10l));

        JSONTokenizer tokenizer = new JSONTokenizer(JSONTokenizer.writeObject(json));
        JSONType object = tokenizer.parse();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        Assert.assertEquals(2, array.size());

        Assert.assertEquals(json.get("key"), array.get("key"));
        Assert.assertEquals(json.get("number"), array.get("number"));

        Assert.assertNull(tokenizer.parse());

        System.out.println("Success: Valid JSON information");
    }

    /**
     *
     */
    @Test
    public void testJSONArrayWrite() {
        System.out.println("--[[ Write JSONArray ]] --");

        JSONArray json = new JSONArray();
        json.add(JSONType.createString("value"));
        json.add(JSONType.createNumber(10l));

        JSONTokenizer tokenizer = new JSONTokenizer(JSONTokenizer.writeArray(json));
        JSONType object = tokenizer.parse();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        Assert.assertEquals(2, array.size());

        for (int i = 0; i < array.size(); i++) {
            Assert.assertEquals(json.get(i), array.get(i));
        }

        Assert.assertNull(tokenizer.parse());

        System.out.println("Success: Valid JSON information");
    }

    /**
     *
     */
    @Test
    public void testSingleUnits() {
        System.out.println("--[[ Single Units ]] --");
        JSONTokenizer tokenizer;

        tokenizer = new JSONTokenizer("null");
        Assert.assertEquals(JSONType.createNull(), tokenizer.parse());
        System.out.println("Success: Null object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("44332211");
        Assert.assertEquals(JSONType.createNumber(44332211l), tokenizer.parse());
        System.out.println("Success: Long object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("1.23456789");
        Assert.assertEquals(JSONType.createDecimal(1.23456789), tokenizer.parse());
        System.out.println("Success: Double object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("\"String_value!\\n\"");
        Assert.assertEquals(JSONType.createString("String_value!\n"), tokenizer.parse());
        System.out.println("Success: String object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("[]");
        Assert.assertEquals(JSONType.createArray(new JSONArray()), tokenizer.parse());
        System.out.println("Success: Array object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("{}");
        Assert.assertEquals(JSONType.createObject(new JSONObject()), tokenizer.parse());
        System.out.println("Success: Object object");
        Assert.assertNull(tokenizer.parse());

        tokenizer = new JSONTokenizer("true");
        Assert.assertEquals(JSONType.createBoolean(true), tokenizer.parse());
        System.out.println("Success: Boolean object");
        Assert.assertNull(tokenizer.parse());
    }

    @Test
    public void testWriteString() {
        System.out.println("--[[ Write String ]] --");
        final String STRING_VALUE = "\"{Word\\n\\\"P/e\\\\r\\\"\\nLine!\\n\"";

        Assert.assertEquals(STRING_VALUE, JSONTokenizer.writeString("{Word\n\"P/e\\r\"\nLine!\n"));
    }

    @Test
    public void testJSONExceptions() {
        System.out.println("--[[ Exceptions ]] --");
        JSONTokenizer tokenizer;

        try {
            tokenizer = new JSONTokenizer("\t\n\r [null, 0,]");
            tokenizer.parse();

            Assert.fail("No exception was thrown");
        } catch (JSONException e) {
            System.out.printf("Success: Exception thrown | %s%n", e.getMessage());
        }
    }
}