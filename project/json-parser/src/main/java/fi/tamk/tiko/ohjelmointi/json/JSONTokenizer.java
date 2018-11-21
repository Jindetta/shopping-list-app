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
     * Stores line number.
     */
    private int lineNumber;

    /**
     * Stores line index.
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
     * Skips comments.
     *
     * @param character Character token.
     * @param mode      Previous mode.
     * @return true if comment was found, otherwise false.
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

            lineIndex++;

            switch (character) {
                case '\r':
                case '\n':
                    isComment.set(false);
                    lineIndex = 0;
                    lineNumber++;
                    continue;

                case ' ':
                case '\t':
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
     * Parses JSON array.
     *
     * @return JSONType.
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
     * Parses JSON object.
     *
     * @return JSONType.
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
     * Parses JSON literal.
     *
     * @return JSONType.
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
     * Parses JSON string.
     *
     * @param quoteType Quote type.
     * @return JSONType.
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

    /**
     * Checks if next token exists.
     *
     * @return true if next token is found, otherwise false.
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
     * Parses next token.
     *
     * @param topLevel Top level element.
     * @return JSONType.
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
                topLevel && value != null && skipWhitespace() != -1,
                "Malformed structure - missing <EOF> at line: %d, %d", lineNumber, lineIndex
            );
        }

        return value;
    }

    /**
     * Parses JSON data.
     *
     * @return JSONType.
     */
    public JSONType parse() {
        JSONType value = parseToken(true);

        if (!identifiers.empty()) {
            popIdentifier('_', true);
        }

        return value;
    }

    /**
     * Overrides default constructor.
     *
     * @param stream Parseable string.
     */
    public JSONTokenizer(String stream) {
        input = stream.replaceAll("\r\n?", "\n");
        identifiers = new Stack<>();

        lineNumber = 1;
        lineIndex = 0;
        position = 0;
    }

    /**
     * Throws exception on error.
     * 
     * @param message Message formatting.
     * @param args    Arguments.
     */
    private static void onError(String message, Object... args) {
        throw new JSONException(message, args);
    }

    /**
     * Throws exception on condition.
     *
     * @param condition Condition.
     * @param message   Message formatting.
     * @param args      Arguments.
     */
    private static void onError(boolean condition, String message, Object... args) {
        if (condition) {
            onError(message, args);
        }
    }

    /**
     * Writes JSON number.
     *
     * @param value Long value.
     * @return JSON formatted String.
     */
    public static String writeNumber(Long value) {
        return String.valueOf(value);
    }

    /**
     * Writes JSON decimal.
     *
     * @param value Double value.
     * @return JSON formatted String.
     */
    public static String writeDecimal(Double value) {
        return String.valueOf(value);
    }

    /**
     * Writes JSON string.
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
     * Writes JSON object.
     *
     * @param object JSONObject.
     * @return JSON formatted string.
     */
    public static String writeObject(JSONObject object) {
        AtomicBoolean hasPrevious = new AtomicBoolean();
        StringBuilder output = new StringBuilder("{");

        object.forEach((key, value) -> {
            if (hasPrevious.getAndSet(true)) {
                output.append(",");
            }

            output.append(String.format("%s:%s", writeString(key), value));
        });

        return output.append("}").toString();
    }

    /**
     * Writes JSON array.
     *
     * @param array JSONArray.
     * @return JSON formatted string.
     */
    public static String writeArray(JSONArray array) {
        AtomicBoolean hasPrevious = new AtomicBoolean();
        StringBuilder output = new StringBuilder("[");

        array.stream().forEach(value -> {
            if (hasPrevious.getAndSet(true)) {
                output.append(",");
            }

            output.append(value);
        });

        return output.append("]").toString();
    }

    /**
     * Writes JSON boolean.
     *
     * @param value Boolean value.
     * @return JSON formatted string.
     */
    public static String writeBoolean(Boolean value) {
        return value.toString();
    }

    /**
     * Writes JSON null.
     *
     * @return JSON formatted string.
     */
    public static String writeNull() {
        return "null";
    }
}