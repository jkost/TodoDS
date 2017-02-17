package todods.TodoDS;

import net.java.html.junit.BrowserRunner;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.Test;

/** Tests for behavior of your application in isolation. Verify
 * behavior of your MVVC code in a unit test.
 */
@RunWith(BrowserRunner.class)
public class DataModelTest {
    @Test public void testUIModelWithoutUI() {
        Task model = new Task(1, "Finish article!", 10, "10/03/2017", true, 2, "", false);
        assertEquals(10, model.getPriority());
    }
}
