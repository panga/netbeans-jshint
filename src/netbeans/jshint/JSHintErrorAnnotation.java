package netbeans.jshint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 * @author Leonardo Zanivan <leonardo.zanivan@gmail.com>
 */
public class JSHintErrorAnnotation extends Annotation {

    private static final Map<DataObject, List<Annotation>> annotations = new HashMap<DataObject, List<Annotation>>();

    private final String reason;
    private final int character;

    private JSHintErrorAnnotation(final String reason, final int character) {
        this.reason = reason;
        this.character = character;
    }

    public static List<Annotation> getAnnotationList(DataObject dataObject) {
        if (null == annotations.get(dataObject)) {
            annotations.put(dataObject, new ArrayList<Annotation>());
        }
        return annotations.get(dataObject);
    }

    public static void clear(DataObject dataObject) {
        for (Annotation annotation : getAnnotationList(dataObject)) {
            annotation.detach();
        }
        getAnnotationList(dataObject).clear();
    }

    public static void remove(DataObject dataObject, JSHintErrorAnnotation annotation) {
        getAnnotationList(dataObject).remove(annotation);
    }

    @Override
    public String getAnnotationType() {
        return "jshint-annotation";
    }

    @Override
    public String getShortDescription() {
        return reason + " (" + "Column: " + character + ")";
    }

    public static void createAnnotation(final DataObject dataObject, final LineCookie lineCookie, final JSHintError error) {
        final Line currentLine = lineCookie.getLineSet().getCurrent(error.getLine() - 1);
        final Line.Part currentPartLine = currentLine.createPart(error.getCharacter() - 1, error.getLength());

        final JSHintErrorAnnotation annotation = new JSHintErrorAnnotation(error.getReason(), error.getCharacter());
        getAnnotationList(dataObject).add(annotation);

        annotation.attach(currentPartLine);

        currentPartLine.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent ev) {
                String type = ev.getPropertyName();
                if (type == null || type.equals(Annotatable.PROP_TEXT)) {
                    currentPartLine.removePropertyChangeListener(this);
                    annotation.detach();
                    JSHintErrorAnnotation.remove(dataObject, annotation);
                }
            }
        });
    }

}
