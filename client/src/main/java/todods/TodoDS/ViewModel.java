package todods.TodoDS;

import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import todods.TodoDS.js.Dialogs;

@Model(className = "Task", targetId="", properties = {
    @Property(name = "id", type = int.class),
    @Property(name = "description", type = String.class),
    @Property(name = "priority", type = int.class),
    @Property(name = "dueDate", type = String.class),
    @Property(name = "alert", type = boolean.class),
    @Property(name = "daysBefore", type = int.class),
    @Property(name = "obs", type = String.class),
    @Property(name = "completed", type = boolean.class)
})
final class ViewModel {
    private static Task task;
    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        task = new Task();
        task.setPriority(10);
        task.setDescription("Finish article!");
        task.setAlert(true);
        task.setDueDate("10/03/2017");
        task.applyBindings();
    }
}
