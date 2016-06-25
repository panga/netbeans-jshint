package netbeans.jshint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 * @author Leonardo Zanivan <leonardo.zanivan@gmail.com>
 */
public class JSHintTest {

    @Test
    public void testLintWithNullConfig() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("test.js");
        PrintWriter out = (new PrintWriter(fo.getOutputStream()));
        out.write("x;");
        out.close();

        LinkedList<JSHintError> errors = JSHint.lint(fo, null);
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testLintWithDefaultConfig() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject fo = fs.getRoot().createData("test.js");
        PrintWriter out = (new PrintWriter(fo.getOutputStream()));
        out.write("x;");
        out.close();

        LinkedList<JSHintError> errors = JSHint.lint(fo, "{}");
        JSHintError head = errors.element();

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(1 == head.getLine());
        Assert.assertEquals("Expected an assignment or function call and instead saw an expression.", head.getReason());
    }

    @Test
    public void testLintWithFileConfig() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();

        FileObject jsFo = fs.getRoot().createData("test.js");
        PrintWriter jsOut = (new PrintWriter(jsFo.getOutputStream()));
        jsOut.write("while (foo)\n  bar();");
        jsOut.close();

        FileObject configFo = fs.getRoot().createData(".jshintrc");
        PrintWriter configOut = (new PrintWriter(configFo.getOutputStream()));
        configOut.write("{\"curly\":true,\"undef\":true,\"globals\": {\"bar\": false}}");
        configOut.close();

        final String jsonConfig = JSHintUtil.getJsonConfig(configFo);

        LinkedList<JSHintError> errors = JSHint.lint(jsFo, jsonConfig);

        Assert.assertEquals(2, errors.size());
        JSHintError error = errors.pop();
        Assert.assertEquals("'foo' is not defined.", error.getReason());
        Assert.assertEquals(1, error.getLine());
        Assert.assertEquals(8, error.getCharacter());
        Assert.assertEquals(3, error.getLength());
        error = errors.pop();
        Assert.assertEquals("Expected '{' and instead saw 'bar'.", error.getReason());
        Assert.assertEquals(2, error.getLine());
        Assert.assertEquals(3, error.getCharacter());
        Assert.assertEquals(1, error.getLength());
    }

}
