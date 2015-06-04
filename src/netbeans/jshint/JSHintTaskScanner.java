package netbeans.jshint;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * @author Leonardo Zanivan <leonardo.zanivan@gmail.com>
 */
public class JSHintTaskScanner extends FileTaskScanner {

    private static final String GROUP_NAME = "jshint-tasklist";
    private Callback callback = null;

    public JSHintTaskScanner(String displayName, String description) {
        super(displayName, description, null);
    }

    public static JSHintTaskScanner create() {
        return new JSHintTaskScanner(
                NbBundle.getMessage(JSHintTaskScanner.class, "LBL_task"),
                NbBundle.getMessage(JSHintTaskScanner.class, "DESC_task")
        );
    }

    @Override
    public List<? extends Task> scan(FileObject fo) {
        if (fo.isFolder() || !fo.getExt().equalsIgnoreCase("js")) {
            return Collections.<Task>emptyList();
        }

        DataObject dataObject = null;
        LineCookie lineCookie = null;
        try {
            dataObject = DataObject.find(fo);
            lineCookie = dataObject.getLookup().lookup(LineCookie.class);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (dataObject != null) {
            JSHintErrorAnnotation.clear(dataObject);
        }

        final LinkedList<Task> tasks = new LinkedList<Task>();

        final LinkedList<JSHintError> errors = JSHint.lint(fo);

        for (JSHintError error : errors) {
            if (dataObject != null && lineCookie != null) {
                JSHintErrorAnnotation.createAnnotation(dataObject, lineCookie, error);
            }
            tasks.add(Task.create(fo, GROUP_NAME, error.getReason(), error.getLine()));
        }

        return tasks;
    }

    @Override
    public void attach(Callback callback) {
        this.callback = callback;
    }

}
