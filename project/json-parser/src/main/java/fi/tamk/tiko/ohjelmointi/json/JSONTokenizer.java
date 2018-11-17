package fi.tamk.tiko.ohjelmointi.json;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Stack;

/**
 * Tokenizes JSON formatted data.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONTokenizer {

    /**
     * Stores current position.
     */
    private int position;

    /**
     * Stores input data.
     */
    private String input;

    /**
     * Stores special identifiers.
     */
    private Stack<JSONIdentifier> identifiers;

    /**
     * Adds a new identifier.
     *
     * @param identifier Identifier value.
     * @see Character
     */
    public void pushIdentifier(char identifier, String message, Object... args) {
        identifiers.push(new JSONIdentifier(identifier, message, args));
    }

    /**
     * Removes given identifier.
     *
     * @param identifier Identifier value.
     * @param forceThrow
     */
    public void popIdentifier(char identifier, boolean forceThrow) {
        JSONException exception = null;

        if (!identifiers.empty()) {
            JSONIdentifier lastIdentifier = identifiers.pop();

            if (!forceThrow && identifier == lastIdentifier.getIdentifier()) {
                return;
            }

            exception = lastIdentifier.getException();
        }

        onError(exception == null, "Malformed identifier - missing <%c> at position: %d", identifier, position);
        throw exception;
    }

    /**
     * Finds next valid token.
     *
     * @param skipToEOL Skip to next newline.
     * @return Valid token or -1.
     */
    private int skipWhitespace(boolean skipToEOL) {
        while (position < input.length()) {
            int character = input.charAt(position++);

            if (!skipToEOL) {
                switch (character) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        continue;

                    case '#':
                        skipWhitespace(true);
                        continue;

                    case '/':
                        if (position < input.length()) {
                            switch (input.charAt(position)) {
                                case '*':
                                    int endIndex = input.indexOf("*/", position++);
                                    onError(endIndex == -1, "Malformed comment - missing <*/> at position: %d", position);
                                    position = endIndex + 2;
                                    continue;

                                case '/':
                                    skipWhitespace(true);
                                    continue;
                            }
                        }

                    default:
                        return character;
                }
            } else if (character == '\r' || character == '\n') {
                return character;
            }
        }

        return -1;
    }

    /**
     * Parses single Unicode character.
     *
     * @return Valid character.
     */
    private char parseUnicode() {
        final int UNICODE_SIZE = 4;
        int endIndex = position + UNICODE_SIZE;

        if (endIndex <= input.length()) {
            String value = input.substring(position, endIndex).toLowerCase();

            if (value.matches("^[\\da-f]+$")) {
                int result = 0;

                for (int i = 0; i < value.length(); i++) {
                    char key = value.charAt(i);
                    result *= 16;

                    if (key >= 'a' && key <= 'f') {
                        result += 10 + (key - 'a');
                    } else {
                        result += key - '0';
                    }

                    position++;
                }

                return (char) result;
            }
        }

        throw new JSONException("Malformed character - invalid <unicode sequence> at position: %d", position);
    }

    /**
     * Validates given literal.
     *
     * @param literal Literal value.
     * @return Literal as JSONType.
     */
    private JSONType getValidatedLiteral(String literal) {
        if (literal != null && !literal.isEmpty()) {
            if (literal.matches("^[+\\-]?(?:0|[1-9]\\d*)(?:[eE][+\\-]?\\d+)?$")) {
                return JSONType.createNumber(Long.parseLong(literal));
            } else if (literal.matches("^[+\\-]?(?:0|[1-9]\\d*)(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?$")) {
                return JSONType.createDecimal(Double.parseDouble(literal));
            } else if (literal.matches("^(?i:(true|false))$")) {
                return JSONType.createBoolean(Boolean.parseBoolean(literal));
            } else if (literal.equalsIgnoreCase("null")) {
                return JSONType.createNull();
            }
        }

        throw new JSONException("Malformed literal - illegal value <%s> at position: %d", literal, position);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseArray() {
        pushIdentifier(']', "Malformed array - missing <]> at position: %d", position);

        JSONArray array = new JSONArray();
        JSONType value = null;

        while ((value = parseToken(false)) != null) {
            if (hasNext()) {
                pushIdentifier(',', "Malformed array - missing <,> at position: %d", position);
            }

            array.add(value);
        }

        return JSONType.createArray(array);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseObject() {
        pushIdentifier('}', "Malformed object - missing <}> at position: %d", position);

        JSONObject object = new JSONObject();
        JSONType value = null;
        String key = null;

        while ((value = parseToken(false)) != null) {
            if (key == null) {
                try {
                    key = value.getAsString();
                    pushIdentifier(':', "Malformed object - missing <:> at position: %d", position);

                    continue;
                } catch (ClassCastException e) {
                    onError("Malformed object - missing <key> at position: %d", position);
                }
            }

            if (hasNext()) {
                pushIdentifier(',', "Malformed object - missing <,> at position: %d", position);
            }

            object.put(key, value);
            key = null;
        }

        onError(key != null, "Malformed object - missing <value> at position: %d", position);
        return JSONType.createObject(object);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseLiteral() {
        int startPosition = --position;
        String value = null;

        do {
            if (position < input.length()) {
                switch (input.charAt(position)) {
                    case ',':
                    case ']':
                    case '}':
                    case ' ':
                    case '\t':
                        value = input.substring(startPosition, position);
                        break;

                    case '\r':
                    case '\n':
                        onError("Malformed literal - illegal <newline> at position: %d", position);

                    default:
                        position++;
                }
            } else {
                value = input.substring(startPosition);
            }
        } while (value == null);

        return getValidatedLiteral(value.trim());
    }

    /**
     *
     *
     * @param quoteType
     * @return
     */
    private JSONType parseString(char quoteType) {
        StringBuilder output = new StringBuilder();
        boolean escapeString = false;

        while (position < input.length()) {
            char key = input.charAt(position++);
            onError(key == '\r' || key == '\n', "Malformed string - illegal <newline> at position: %d", position);

            if (escapeString) {
                escapeString = false;

                switch (key) {
                    case 'r': key = '\r'; break;
                    case 'n': key = '\n'; break;
                    case 't': key = '\t'; break;
                    case 'b': key = '\b'; break;
                    case 'f': key = '\f'; break;
                    case 'u': key = parseUnicode(); break;

                    case '"':
                    case '\'':
                    case '\\':
                    case '/':
                        break;

                    default:
                        onError("Malformed string - unexpected <%c> at position: %d", key, position);
                }
            } else if (key == quoteType) {
                return JSONType.createString(output.toString());
            } else if (key == '\\') {
                escapeString = true;
                continue;
            }

            output.append(key);
        }

        throw new JSONException("Malformed string - invalid <string> at position: %d", position);
    }

    /*private String simplifyJSONData() {
        String data = input.substring(position).replaceAll("\\s", "");
        data = data.replaceAll("(\"|').*?(\\1)", "$1");

        return data.replaceAll("(.).*?([,}]]|$)", "$1");
    }*/

    /**
     *
     *
     * @return
     */
    private boolean hasNext() {
        int storedPosition = position;
        int token = skipWhitespace(false);
        boolean hasNext = false;

        switch (token) {
            case ',':
                hasNext = hasNext();

            case -1:
            case ']':
            case '}':
                break;

            default:
                hasNext = true;
        }

        position = storedPosition;
        return hasNext;
    }

    /**
     *
     *
     * @param topLevel
     * @return
     */
    private JSONType parseToken(boolean topLevel) {
        int token = skipWhitespace(false);
        JSONType value = null;

        if (token != -1) {
            switch (token) {
                case '[':
                    value = parseArray();
                    break;

                case '{':
                    value = parseObject();
                    break;

                case '"':
                case '\'':
                    value = parseString((char) token);
                    break;

                default:
                    value = parseLiteral();
                    break;

                case ',':
                case ':':
                case ']':
                case '}':
                    popIdentifier((char) token, false);

                    if (token == ',' || token == ':') {
                        value = parseToken(topLevel);
                    }
            }

            onError(
                topLevel && value != null && skipVoidTokens(false) != -1,
                "Malformed identifier - illegal <%c> at position: %d", (char) token, position
            );
        }

        return value;
    }

    /**
     *
     *
     * @return
     */
    public JSONType parse() {
        JSONType value = parseToken(true);

        if (!identifiers.empty()) {
            popIdentifier('_', true);
        }

        return value;
    }

    /**
     *
     *
     * @param stream
     */
    public JSONTokenizer(String stream) {
        identifiers = new Stack<>();

        input = stream;
        position = 0;
    }

    /**
     * 
     * 
     * @param message
     * @param args
     */
    private static void onError(String message, Object... args) {
        throw new JSONException(message, args);
    }

    /**
     * 
     * @param condition
     * @param message
     * @param args
     */
    private static void onError(boolean condition, String message, Object... args) {
        if (condition) {
            onError(message, args);
        }
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeNumber(Long value) {
        return String.valueOf(value);
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeDecimal(Double value) {
        return String.valueOf(value);
    }

    /**
     * Writes JSON formatted String.
     *
     * @param value     String value.
     * @param quoteType Quote type.
     * @return JSON formatted String.
     */
    private static String writeString(String value, final String quoteType) {
        StringBuilder output = new StringBuilder(quoteType);

        for (int i = 0; i < value.length(); i++) {
            String key = value.substring(i, i + 1);

            if (!key.equals(quoteType)) {
                switch (key) {
                    case "\t": key = "\\t"; break;
                    case "\r": key = "\\r"; break;
                    case "\n": key = "\\n"; break;
                    case "\f": key = "\\f"; break;
                    case "\b": key = "\\b"; break;
                    case "\"": key = "\\\""; break;
                    case "\\": key = "\\\\"; break;
                }
            } else {
                output.append("\\");
            }

            output.append(key);
        }

        return output.append(quoteType).toString();
    }

    /**
     * Writes JSON formatted String with double-quotes.
     *
     * @param value String value.
     * @return JSON formatted String.
     */
    public static String writeString(String value) {
        return writeString(value, "\"");
    }

    /**
     * Writes JSON formatted String with single-quotes.
     *
     * @param value String value.
     * @return JSON formatted String.
     */
    public static String writeSingleQuoteString(String value) {
        return writeString(value, "'");
    }

    /**
     *
     *
     * @param object
     * @return
     */
    public static String writeObject(JSONObject object) {
        AtomicBoolean hasPreviousItem = new AtomicBoolean();
        StringBuilder output = new StringBuilder("{");

        object.forEach((key, value) -> {
            if (hasPreviousItem.get()) {
                output.append(",");
            }

            output.append(String.format("%s:%s", writeString(key), value));
            hasPreviousItem.set(true);
        });

        return output.append("}").toString();
    }

    /**
     *
     *
     * @param array
     * @return
     */
    public static String writeArray(JSONArray array) {
        AtomicBoolean hasPreviousItem = new AtomicBoolean();
        StringBuilder output = new StringBuilder("[");

        array.stream().forEach(value -> {
            if (hasPreviousItem.get()) {
                output.append(",");
            }

            output.append(value);
            hasPreviousItem.set(true);
        });

        return output.append("]").toString();
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeBoolean(Boolean value) {
        return value.toString();
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeNull() {
        return "null";
    }
}