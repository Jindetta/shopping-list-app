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
     * 
     */
    private int lineNumber;

    /**
     * 
     */
    private int lineIndex;

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

        onError(exception == null, "Malformed identifier - missing <%c> at line: %d, %d", identifier, lineNumber, lineIndex);
        throw exception;
    }

    /**
     * 
     * @param character
     * @return
     */
    private boolean skipComment(int character, AtomicBoolean mode) {
        if (!mode.get() && character == '/') {
            if (position < input.length()) {
                switch (input.charAt(position)) {
                    case '*':
                        int endIndex = input.indexOf("*/", ++position);
                        onError(endIndex == -1, "Malformed comment - missing <*/> at EOF");
                        String substring = input.substring(position, endIndex);
                        position = endIndex + 2;

                        lineNumber += substring.replaceAll("[^\n]", "").length();
                        lineIndex = position - substring.lastIndexOf("\n");

                        return true;

                    case '/':
                        mode.set(true);
                        return true;
                }
            }

            return false;
        }

        mode.set(true);
        return true;
    }

    /**
     * Finds next valid token.
     *
     * @return Valid token or -1.
     */
    private int skipWhitespace() {
        AtomicBoolean isComment = new AtomicBoolean();

        while (position < input.length()) {
            int character = input.charAt(position++);

            switch (character) {
                case '\r':
                case '\n':
                    lineNumber++;
                    isComment.set(false);
                    lineIndex = 0;
                    continue;

                case ' ':
                case '\t':
                    lineIndex++;
                    continue;

                case '#':
                case '/':
                    if (skipComment(character, isComment)) {
                        continue;
                    }
            }

            if (!isComment.get()) {
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

                    lineIndex++;
                    position++;
                }

                return (char) result;
            }
        }

        throw new JSONException("Malformed character - invalid <unicode sequence> at line: %d, %d", lineNumber, lineIndex);
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

        throw new JSONException("Malformed literal - illegal value <%s> at line: %d, %d", literal, lineNumber, lineIndex);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseArray() {
        pushIdentifier(']', "Malformed array - missing <]> at line: %d, %d", lineNumber, lineIndex);

        JSONArray array = new JSONArray();
        JSONType value = null;

        while ((value = parseToken(false)) != null) {
            if (hasNext()) {
                pushIdentifier(',', "Malformed array - missing <,> at line: %d, %d", lineNumber, lineIndex);
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
        pushIdentifier('}', "Malformed object - missing <}> at line: %d, %d", lineNumber, lineIndex);

        JSONObject object = new JSONObject();
        JSONType value = null;
        String key = null;

        while ((value = parseToken(false)) != null) {
            if (key == null) {
                try {
                    key = value.getAsString();
                    pushIdentifier(':', "Malformed object - missing <:> at line: %d, %d", lineNumber, lineIndex);

                    continue;
                } catch (ClassCastException e) {
                    onError("Malformed object - missing <key> at line: %d, %d", lineNumber, lineIndex);
                }
            }

            if (hasNext()) {
                pushIdentifier(',', "Malformed object - missing <,> at line: %d, %d", lineNumber, lineIndex);
            }

            object.put(key, value);
            key = null;
        }

        onError(key != null, "Malformed object - missing <value> at line: %d, %d", lineNumber, lineIndex);
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
                        onError("Malformed literal - illegal <newline> at line: %d, %d", lineNumber, lineIndex);

                    default:
                        lineIndex++;
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
        JSONType value = null;

        while (value == null && position < input.length()) {
            char key = input.charAt(position++);
            onError(key == '\r' || key == '\n', "Malformed string - illegal <newline> at line: %d, %d", lineNumber, lineIndex);

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
                        onError("Malformed string - unexpected <%c> at line: %d, %d", key, lineNumber, lineIndex);
                }

                output.append(key);
            } else if (key == quoteType) {
                value = JSONType.createString(output.toString());
            } else if (key == '\\') {
                escapeString = true;
            } else {
                output.append(key);
            }

            lineIndex++;
        }

        onError(value == null, "Malformed string - invalid <string> at line: %d, %d", lineNumber, lineIndex);

        return value;
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
        int token = skipWhitespace();
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
        int token = skipWhitespace();
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
                topLevel && value != null && hasNext(),
                "Malformed identifier - illegal <%c> at line: %d, %d", (char) token, lineNumber, lineIndex
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
        input = stream.replaceAll("\r\n?", "\n");
        identifiers = new Stack<>();

        lineNumber = 1;
        lineIndex = 0;
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