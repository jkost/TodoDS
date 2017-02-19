package todods.TodoDS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import static java.util.stream.Collectors.toList;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import todods.TodoDS.js.Dialogs;

@Model(className = "TaskList", targetId = "", properties = {
    @Property(name = "tasks", type = Task.class, array = true)
})
final class ViewModel {

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        Task task = new Task();
        task.setPriority(10);
        task.setDescription("Finish TodoDS article!");
        task.setAlert(true);
        task.setDueDate("10/03/2017");
        TaskList taskList = new TaskList();
        taskList.getTasks().add(task);
        taskList.getTasks().add(new Task(2, "Book venue!", 7, "01/04/2017", false, 2, "", false));
        taskList.applyBindings();
    }
    
    @Model(className = "Task", targetId = "", properties = {
        @Property(name = "id", type = int.class),
        @Property(name = "description", type = String.class),
        @Property(name = "priority", type = int.class),
        @Property(name = "dueDate", type = String.class),
        @Property(name = "alert", type = boolean.class),
        @Property(name = "daysBefore", type = int.class),
        @Property(name = "obs", type = String.class),
        @Property(name = "completed", type = boolean.class)
    })
    public static class TaskModel {  
        
    }
}