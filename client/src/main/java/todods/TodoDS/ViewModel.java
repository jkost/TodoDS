package todods.TodoDS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import static java.util.stream.Collectors.toList;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import todods.TodoDS.js.Dialogs;

@Model(className = "TaskList", targetId = "", properties = {
    @Property(name = "editing", type = Task.class),
    @Property(name = "tasks", type = Task.class, array = true)
})
final class ViewModel {

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        Task task = new Task();
        task.setPriority(10);
        task.setDescription("Finish article!");
        task.setAlert(true);
        task.setDueDate("10/03/2017");
        TaskList taskList = new TaskList();
        taskList.getTasks().add(task);
        taskList.getTasks().add(new Task(2, "Book venue!", 7, "11/01/2017", true, 2, "", false));
        taskList.applyBindings();
    }
    
    @Function
    public static void edit(TaskList tasks, Task data) {
        tasks.setEditing(data);
    }   

    @Function
    public static void commit(TaskList tasks, Task data) {
        tasks.setEditing(null);
    }   

    @Function
    public static void cancel(TaskList tasks, Task data) {
        tasks.setEditing(null);
    }       
    
    @Function
    public static void removeTask(TaskList tasks, Task data) {
        tasks.getTasks().remove(data);
    }   
    
    @Function
    public static void sortBy(TaskList tasks) {
        tasks.getTasks().sort(new PriorityComparator());
    }    
    
    @Function
    public static void showCompleted(TaskList tasks) {
        tasks.getTasks().stream().filter(Task::isCompleted).collect(toList());
    }    
    
    private static class PriorityComparator implements Comparator<Task> {

        @Override
        public int compare(final Task t1, final Task t2) {
            if (t1.getPriority() == t2.getPriority()) {
                return 0;
            } else if (t1.getPriority() > t2.getPriority()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private static class DueDateComparator implements Comparator<Task> {

        @Override
        public int compare(final Task t1, final Task t2) {
            return t1.getDueDate().compareTo(t2.getDueDate());
        }
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
        @Function
        public static void markAsCompleted(boolean completed) {
            completed = true;
        }
        
        @ComputedProperty
        public static boolean isLate(String dueDate) {
            if (dueDate == null || dueDate.isEmpty()) {
                return false;
            }
            LocalDate dateDue = LocalDate.parse(dueDate,
                    DateTimeFormatter.ofPattern("d/MM/yyyy"));
            return (dateDue == null) ? false : dateDue.compareTo(LocalDate.now()) < 0;
        }

        @ComputedProperty
        public static boolean hasAlert(String dueDate, boolean alert, int daysBefore) {
            if (dueDate == null || dueDate.isEmpty()) {
                return false;
            }
            LocalDate dateDue = LocalDate.parse(dueDate,
                    DateTimeFormatter.ofPattern("d/MM/yyyy"));
            if (!alert || dateDue == null) {
                return false;
            } else {
                int dias = dateDue.getDayOfYear() - LocalDate.now().getDayOfYear();
                return dias <= daysBefore;
            }
        }
    }
}

