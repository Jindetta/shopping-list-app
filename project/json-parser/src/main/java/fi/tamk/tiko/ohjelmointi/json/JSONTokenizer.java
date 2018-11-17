package fi.tamk.tiko.ohjelmointi.json;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
    private AtomicInteger position;

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

        onError(exception == null, "Malformed identifier - missing <%c> at position: %d", identifier, position.get());
        throw exception;
    }

    /**
     * Finds next valid token.
     *
     * @param skipToEOL Skip to next newline.
     * @return Valid token or -1.
     */
    private int skipVoidTokens(boolean skipToEOL) {
        while (position.get() < input.length()) {
            int character = input.charAt(position.getAndIncrement());

            if (!skipToEOL) {
                switch (character) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        continue;

                    case '#':
                        skipVoidTokens(true);
                        continue;

                    case '/':
                        if (position.get() < input.length()) {
                            switch (input.charAt(position.get())) {
                                case '*':
                                    int endIndex = input.indexOf("*/", position.incrementAndGet());
                                    onError(endIndex == -1, "Malformed comment - missing <*/> at position: %d", position.get());
                                    position.set(endIndex + 2);
                                    continue;

                                case '/':
                                    skipVoidTokens(true);
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
        int endIndex = position.get() + UNICODE_SIZE;

        if (endIndex <= input.length()) {
            String value = input.substring(position.get(), endIndex).toLowerCase();

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

                    position.getAndIncrement();
                }

                return (char) result;
            }
        }

        throw new JSONException("Malformed character - invalid <unicode sequence> at position: %d", position.get());
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

        throw new JSONException("Malformed literal - illegal value <%s> at position: %d", literal, position.get());
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseArray() {
        JSONTokenizer lexer = new JSONTokenizer(input, position, identifiers);
        lexer.pushIdentifier(']', "Malformed array - missing <]> at position: %d", position.get());

        JSONArray array = new JSONArray();
        JSONType value = null;

        while ((value = lexer.parseToken(false)) != null) {
            array.add(value);
            lexer.pushIdentifier(',', "Malformed array - missing <,> at position: %d", position.get());
        }

        return JSONType.createArray(array);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseObject() {
        JSONTokenizer lexer = new JSONTokenizer(input, position, identifiers);
        lexer.pushIdentifier('}', "Malformed object - missing <}> at position: %d", position.get());

        JSONObject object = new JSONObject();
        JSONType value = null;
        String key = null;

        while ((value = lexer.parseToken(false)) != null) {
            if (key == null) {
                try {
                    key = value.getAsString();
                    lexer.pushIdentifier(':', "Malformed object - missing <:> at position: %d", position.get());

                    continue;
                } catch (ClassCastException e) {
                    onError("Malformed object - missing <key> at position: %d", position.get());
                }
            }

            if (!object.isEmpty()) {
                lexer.pushIdentifier(',', "Malformed object - missing <,> at position: %d", position.get());
            }

            object.put(key, value);
            key = null;
        }

        onError(key != null, "Malformed object - missing <value> at position: %d", position.get());
        return JSONType.createObject(object);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseLiteral() {
        int startPosition = position.decrementAndGet();
        String value = null;

        do {
            if (position.get() < input.length()) {
                switch (input.charAt(position.get())) {
                    case ',':
                    case ']':
                    case '}':
                    case ' ':
                    case '\t':
                        value = input.substring(startPosition, position.get());
                        break;

                    case '\r':
                    case '\n':
                        onError("Malformed literal - illegal <newline> at position: %d", position.get());

                    default:
                        position.getAndIncrement();
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

        while (position.get() < input.length()) {
            char key = input.charAt(position.getAndIncrement());
            onError(key == '\r' || key == '\n', "Malformed string - illegal <newline> at position: %d", position.get());

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
                        onError("Malformed string - unexpected <%c> at position: %d", key, position.get());
                }
            } else if (key == quoteType) {
                return JSONType.createString(output.toString());
            } else if (key == '\\') {
                escapeString = true;
                continue;
            }

            output.append(key);
        }

        throw new JSONException("Malformed string - invalid <string> at position: %d", position.get());
    }

    /**
     *
     *
     * @return
     */
    private JSONType skipNext() {
        int storedPosition = position.get();
        JSONType token = parseNext();
        position.set(storedPosition);

        return token;
    }

    /**
     *
     *
     * @param topLevel
     * @return
     */
    private JSONType parseToken(boolean topLevel) {
        int token = skipVoidTokens(false);
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

                    break;
            }

            onError(
                topLevel && value != null && skipVoidTokens(false) != -1,
                "Malformed identifier - illegal <%c> at position: %d", (char) token, position.get()
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
     * @param startPosition
     * @param identifiers
     */
    private JSONTokenizer(String stream, AtomicInteger startPosition, Stack<JSONIdentifier> identifiers) {
        this.identifiers = identifiers;
        position = startPosition;
        input = stream;
    }

    /**
     *
     *
     * @param stream
     */
    public JSONTokenizer(String stream) {
        this(stream, new AtomicInteger(), new Stack<>());
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