package netbeans.jshint;

import java.io.IOException;
import java.nio.charset.Charset;
import org.openide.filesystems.FileObject;
import org.netbeans.api.queries.FileEncodingQuery;
import org.openide.util.Exceptions;

/**
 * @author Leonardo Zanivan <leonardo.zanivan@gmail.com>
 */
public final class JSHintUtil {

    private static final String JSHINT_CONFIG = ".jshintrc";

    private JSHintUtil() {
    }

    public static String getFileContent(FileObject fo) throws IOException {
        final Charset charset = FileEncodingQuery.getEncoding(fo);
        return fo.asText(charset.name());
    }

    public static FileObject findFile(String name, FileObject folder) {
        final FileObject fo = folder.getFileObject(name, "");

        if (fo != null && fo.isData()) {
            return fo;
        }

        if (folder.isRoot()) {
            return null;
        }

        return findFile(name, folder.getParent());
    }

    public static String getJsonConfig(FileObject fo) {
        try {
            //TODO: Cache config
            final FileObject config = findFile(JSHINT_CONFIG, fo.getParent());
            if (config == null) {
                return null;
            }

            return getFileContent(config);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }
}
