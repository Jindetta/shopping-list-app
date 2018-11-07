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
}