import org.junit.*;

import fi.tamk.tiko.ohjelmointi.json.*;

public class UnitTest {

    /**
     *
     */
    @Test
    public void testJSONArray() {
        final String JSON_STRING = "['item1', 'item2', 'item3']";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON_STRING);
        JSONType object = tokenizer.parseNext();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.ARRAY, object.getType());

        JSONArray array = object.getAsArray();
        Assert.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String ITEM_STRING = String.format("item%d", i + 1);

            Assert.assertEquals(ITEM_STRING, array.get(i).getAsString());
            System.out.printf("Success: \"%s\" was found at [%d]%n", ITEM_STRING, i);
        }

        Assert.assertNull(tokenizer.parseNext());
    }

    /**
     *
     */
    @Test
    public void testJSONObject() {
        final String JSON_STRING = "{'key1': 'item1', 'key2': 'item2', 'key3': 'item3'}";

        JSONTokenizer tokenizer = new JSONTokenizer(JSON_STRING);
        JSONType object = tokenizer.parseNext();

        Assert.assertTrue(object instanceof JSONType);
        Assert.assertEquals(JSONTypes.OBJECT, object.getType());

        JSONObject array = object.getAsObject();
        Assert.assertEquals(3, array.size());

        for (int i = 0; i < array.size(); i++) {
            final String KEY_STRING = String.format("key%d", i + 1);
            final String ITEM_STRING = String.format("item%d", i + 1);

            Assert.assertTrue(array.containsKey(KEY_STRING));
            Assert.assertEquals(ITEM_STRING, array.get(KEY_STRING).getAsString());
            System.out.printf("Success: \"%s\" was found at [\"%s\"]%n", ITEM_STRING, KEY_STRING);
        }

        Assert.assertNull(tokenizer.parseNext());
    }
}