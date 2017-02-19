package todods.TodoDS.js;

import net.java.html.js.JavaScriptBody;
import java.util.List;

/** Use {@link JavaScriptBody} annotation on methods to
 * directly interact with JavaScript. See
 * http://bits.netbeans.org/html+java/1.2/net/java/html/js/package-summary.html
 * to understand how.
 */
public final class Dialogs {
    private Dialogs() {
    }
    
    @JavaScriptBody(
            args = {"expiredTasks"},
            body = "for (task in expiredTasks) {\n"
            + " window.alert('Task: ' + task.getDescription() + '\nexpired on ' + task.getDueDate());\n"
            + "}\n"
    )
    public static native void showAlerts(List expiredTasks);
    
}
