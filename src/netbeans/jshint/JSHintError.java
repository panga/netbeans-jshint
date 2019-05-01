package netbeans.jshint;

import org.mozilla.javascript.NativeObject;

/**
 * @author Leonardo Zanivan <panga@apache.org>
 */
public class JSHintError {

    private final String reason;
    private final int line;
    private final int character;
    private final int length;

    public JSHintError(NativeObject error) {
        reason = error.get("reason", error).toString();
        line = ((Number) error.get("line", error)).intValue();
        character = ((Number) error.get("character", error)).intValue();

        final Object a = error.get("a", error);
        if (a != null && a instanceof CharSequence) {
            length = ((CharSequence) a).length();
        } else {
            length = 1;
        }
    }

    public String getReason() {
        return reason;
    }

    public int getLine() {
        return line;
    }

    public int getCharacter() {
        return character;
    }

    public int getLength() {
        return length;
    }

}
