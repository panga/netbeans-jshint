package netbeans.jshint;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

/**
 * @author Leonardo Zanivan <leonardo.zanivan@gmail.com>
 */
public class JSHintUtilTest {

    @Test
    public void testFindFile() throws IOException {
        FileSystem fs = FileUtil.createMemoryFileSystem();
        FileObject hasFile = fs.getRoot().createFolder("hasFile");
        FileObject file = hasFile.createData("file");
        FileObject childFolder = hasFile.createFolder("childFolder");

        FileObject result = JSHintUtil.findFile("file", childFolder);
        Assert.assertEquals(file, result);
    }

}
