import org.junit.jupiter.api.*;

import fi.tamk.tiko.ohjelmointi.json.*;

public class JSONSingleUnitTest extends Assertions {

    /**
     * Stores JSONTokenizer.
     */
    private JSONTokenizer tokenizer;

    /**
     * Tests for whitespace and comments.
     */
    @Test
    public void testJSONWhitespaceUnit() {
        System.out.println("--[[ Single Unit: Whitespace ]]--");

        final String[] VALID_VALUES = {"// Whitespace", "/* Comment */", "\r\n", " "};

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"/*Unterminated comment", "//\n/*"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON array.
     */
    @Test
    public void testJSONArrayUnit() {
        System.out.println("--[[ Single Unit: Array ]]--");

        final JSONArray ARRAY_DATA = new JSONArray();
        final String[] VALID_VALUES = {"[\r\n]", "\r[\t]\n", " [   \t\r\n]\n"};
        final JSONType ARRAY_TYPE = JSONType.createArray(ARRAY_DATA);

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertEquals(ARRAY_TYPE, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] VALID_PAIRS = {"[\r\"value\"]", "\r['value'\n]"};
        ARRAY_DATA.addString("value");

        for (String value : VALID_PAIRS) {
            tokenizer = new JSONTokenizer(value);

            assertEquals(ARRAY_TYPE, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"[,]", "['key'}", "['key'", "[,\"key\"]", "[", "]"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON object.
     */
    @Test
    public void testJSONObjectUnit() {
        System.out.println("--[[ Single Unit: Object ]]--");

        final JSONObject OBJECT_DATA = new JSONObject();
        final String[] VALID_VALUES = {"{\r\n}", "\r{\t}\n", " {   \t\r\n}\n"};
        final JSONType OBJECT_TYPE = JSONType.createObject(OBJECT_DATA);

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertEquals(OBJECT_TYPE, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        OBJECT_DATA.putString("key", "value");
        final String[] VALID_PAIRS = {"{\r\"key\":\"value\"}", "\r{'key' : 'value'\n}"};

        for (String value : VALID_PAIRS) {
            tokenizer = new JSONTokenizer(value);

            assertEquals(OBJECT_TYPE, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"{0: 'value'}", "{'key''value'}", "{'key':}", "{", "}"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON string.
     */
    @Test
    public void testJSONStringUnit() {
        System.out.println("--[[ Single Unit: String ]]--");

        final String[] VALID_VALUES = {"\\\\", "_\\u2E3A", "\\f\\t", "😂ÄäåÅöÖòóáàÁÀ¨~#[]{}:,\\u2713", "\\\'\\\"(o^o)\\\"\\\'"};
        final String[] RESULTS = {"\\", "_\u2E3A", "\f\t", "😂ÄäåÅöÖòóáàÁÀ¨~#[]{}:,✓", "\'\"(o^o)\"\'"};

        for (int i = 0; i < VALID_VALUES.length; i++) {
            JSONType string = JSONType.createString(RESULTS[i]);
            tokenizer = new JSONTokenizer(String.format("\"%s\"", VALID_VALUES[i]));

            assertEquals(string, tokenizer.parse());
            assertNull(tokenizer.parse());

            tokenizer = new JSONTokenizer(String.format("'%s'", VALID_VALUES[i]));

            assertEquals(string, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"\"value", "'\n'", "\"\r\"", "'no\\x'", "\"value'", "\"\\u1F\""};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON boolean.
     */
    @Test
    public void testJSONBooleanUnit() {
        System.out.println("--[[ Single Unit: Boolean ]]--");

        final String[] VALID_VALUES = {"True\n", "FALSE", "true", "\rtruE", "faLsE", "\tTRUE", "False"};

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);
            Boolean bool = Boolean.parseBoolean(value.trim());

            assertEquals(JSONType.createBoolean(bool), tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"\false", "\true", "fals3", "no", "FAL,SE", "t\rue"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON number.
     */
    @Test
    public void testJSONNumberUnit() {
        System.out.println("--[[ Single Unit: Number ]]--");

        final String[] VALID_VALUES = {"\n143", "-0\r", "+3413", "\n-1104"};

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);
            Long number = Long.parseLong(value.trim());

            assertEquals(JSONType.createNumber(number), tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"4\n,", "F3", "23\r4", "++5", "-\n19"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON decimal.
     */
    @Test
    public void testJSONDecimalUnit() {
        System.out.println("--[[ Single Unit: Decimal ]]--");

        final String[] VALID_VALUES = {"\r1.0\n", "-1.25e5", "3e+5", "+2.3E-8", "-3E2"};

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);
            Double decimal = Double.parseDouble(value.trim());

            assertEquals(JSONType.createDecimal(decimal), tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {".3", "+e5", "9.5+E1", ",\r1.2", ".-4e3", "-\r1e2"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }

    /**
     * Tests for JSON null.
     */
    @Test
    public void testJSONNullUnit() {
        System.out.println("--[[ Single Unit: Null ]]--");

        final String[] VALID_VALUES = {"NULL", "\rNuLL", "null", "nuLL\n"};
        final JSONType NULL_OBJECT = JSONType.createNull();

        for (String value : VALID_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertEquals(NULL_OBJECT, tokenizer.parse());
            assertNull(tokenizer.parse());
        }

        final String[] ERROR_VALUES = {"NUL", "N-ull", " \null,", ",\nnull"};

        for (String value : ERROR_VALUES) {
            tokenizer = new JSONTokenizer(value);

            assertThrows(JSONException.class, () -> tokenizer.parse());
        }

        System.out.println("Success: All tests completed");
    }
}