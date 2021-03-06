package netbeans.jshint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.json.JsonParser;

/**
 * @author Leonardo Zanivan <panga@apache.org>
 */
public final class JSHint {

    private static final String JSHINT_FILE = "jshint-2.10.1.js";
    private static final JSHint INSTANCE = new JSHint();

    private Scriptable scope;
    private Function jshint;

    private JSHint() {
        final Context cx = Context.enter();
        scope = cx.initStandardObjects();

        try {
            jshint = evaluateJSHint(cx, scope);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }
    }

    private LinkedList<JSHintError> lintFile(final FileObject fo, final String jsonConfig) {
        final LinkedList<JSHintError> result = new LinkedList<JSHintError>();

        if (jsonConfig == null) {
            return result;
        }

        try {
            final Context cx = Context.enter();
            final NativeObject config = (NativeObject) new JsonParser(cx, scope).parseValue(jsonConfig);

            final String source = JSHintUtil.getFileContent(fo);

            final NativeObject globals = (NativeObject) config.get("globals");
            if (globals != null) {
                config.delete("globals");
            }

            final Object args[] = {source, config, globals};

            jshint.call(cx, scope, scope, args);

            final NativeArray errors = (NativeArray) jshint.get("errors", null);

            for (Object error : errors) {
                if (error == null) {
                    continue;
                }

                result.push(new JSHintError((NativeObject) error));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JsonParser.ParseException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            Context.exit();
        }

        return result;
    }

    private Function evaluateJSHint(Context cx, Scriptable scope) throws IOException {
        final InputStream stream = getClass().getResourceAsStream(JSHINT_FILE);
        final Reader reader = new BufferedReader(new InputStreamReader(stream));

        cx.evaluateReader(scope, reader, JSHINT_FILE, 1, null);
        return (Function) scope.get("JSHINT", scope);
    }

    public static LinkedList<JSHintError> lint(FileObject fo, final String jsonConfig) {
        return INSTANCE.lintFile(fo, jsonConfig);
    }
}
