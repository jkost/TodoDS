package todods.TodoDS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static java.util.stream.Collectors.toList;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import todods.TodoDS.js.Calendar;
import todods.TodoDS.js.Calendar.LocalDate;

@Model(className = "TaskList", targetId = "", properties = {
    @Property(name = "selected", type = Task.class),
    @Property(name = "edited", type = Task.class),
    @Property(name = "tasks", type = Task.class, array = true),
    @Property(name = "showCompleted", type = boolean.class),
    @Property(name = "sortByPriority", type = boolean.class),
    @Property(name = "dialog", type = boolean.class),
    @Property(name = "message", type = String.class)
})
final class ViewModel {

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        Calendar.create();
        Task task = new Task();
        task.setPriority(1);
        task.setDescription("Finish TodoDS article!");
        task.setAlert(true);
        task.setDueDate("10/03/2017");
        task.setCompleted(false);
        TaskList taskList = new TaskList();
        taskList.setSelected(null);
        taskList.setEdited(null);
        taskList.getTasks().add(task);
        taskList.getTasks().add(new Task(2, "Book venue!", 7, "01/04/2017", false, 2, "", false));
        taskList.getTasks().add(new Task(3, "A completed Task", 3, "01/04/2017", false, 2, "", true));
        taskList.applyBindings();
    }

    @ComputedProperty
    public static List<Task> sortedAndFilteredTasks(List<Task> tasks, boolean sortByPriority, boolean showCompleted) {
        List<Task> result = new ArrayList<>();
        if (showCompleted) {
            result.addAll(tasks.stream().filter(Task::isCompleted).collect(toList()));
        } else {
            result.addAll(tasks);
        }

        if (sortByPriority) {
            result.sort(new PriorityComparator());
        } else {
            result.sort(new DueDateComparator());            
        }
        return result;
    }

    @Function
    static void addNew(TaskList tasks) {
        tasks.setSelected(null);
        tasks.setEdited(new Task());
    }

    @Function
    static void edit(TaskList tasks, Task data) {
        tasks.setSelected(data);
        tasks.setEdited(data.clone());
    }

    @Function
    static void commit(TaskList tasks) {
        final Task task = tasks.getEdited();
        if (task == null) {
            return;
        }
        if (!validate(task)) return;
        final Task selectedTask = tasks.getSelected();
        if (selectedTask != null) {
            tasks.getTasks().set(tasks.getTasks().indexOf(selectedTask), task);
        } else {
            tasks.getTasks().add(task);
        }
        tasks.setEdited(null);
    }
    
    private static boolean validate(Task task) {
        String invalid = null;
        if (task.getValidate() != null) {
            invalid = task.getValidate();
        }
        return invalid == null;
    }

    @Function
    static void cancel(TaskList tasks) {
        tasks.setSelected(null);
        tasks.setEdited(null);
    }

    @Function
    static void removeTask(TaskList tasks, Task data) {
        tasks.getTasks().remove(data);
    }

    @Function
    static void expiredTasks(final TaskList tasks) {
        List<Task> listTasksWithAlert = listTasksWithAlert(tasks.getTasks());
        for (Task task : listTasksWithAlert) {
            showExpiredTask(tasks, task);
        }
    }

    private static void showExpiredTask(TaskList tasks, Task task) {
        tasks.setMessage("Task: " + task.getDescription() + "\nexpired on " + task.getDueDate());
        tasks.setDialog(true);     
    }
    
    @Function
    public static void hideDialog(final TaskList tasks){
        tasks.setDialog(false);
    }

    private static List<Task> listTasksWithAlert(List<Task> tasks) {
        return tasks.stream().filter(Task::isAlert).collect(toList());
    }

    @ComputedProperty
    static int numberOfTasksWithAlert(List<Task> tasks) {
        return listTasksWithAlert(tasks).size();
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
            LocalDate t1DateDue = LocalDate.parse(t1.getDueDate(),"d/MM/yyyy");
            LocalDate t2DateDue = LocalDate.parse(t2.getDueDate(),"d/MM/yyyy");
            return t1DateDue.compareTo(t2DateDue);
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

        @ComputedProperty
        public static boolean isLate(String dueDate) {
            if (dueDate == null || dueDate.isEmpty()) {
                return false;
            }
            LocalDate dateDue = LocalDate.parse(dueDate,"d/MM/yyyy");
            return (dateDue == null) ? false : dateDue.compareTo(LocalDate.now()) < 0;
        }

        @ComputedProperty
        static boolean hasAlert(String dueDate, boolean alert, int daysBefore) {
            if (dueDate == null || dueDate.isEmpty()) {
                return false;
            }
            LocalDate dateDue = LocalDate.parse(dueDate,"d/MM/yyyy");
            if (!alert || dateDue == null) {
                return false;
            } else {
                int dias = dateDue.getDayOfYear() - LocalDate.now().getDayOfYear();
                return dias <= daysBefore;
            }
        }
        
        @ComputedProperty
        static String validate(String description, int priority, String dueDate, int daysBefore) {
            String errorMsg = null;
            if (description == null || description.isEmpty()) {
                errorMsg = "Specify a description";
            }
            if (errorMsg == null && (priority < 1 || priority > 10)) {
                errorMsg = "Priority must be an integer in the range 1-10";
            }
            if (errorMsg == null) {
                if (dueDate == null) {
                    errorMsg = "Specify a valid due date";
                } else {
                    
                    LocalDate dateDue = LocalDate.parse(dueDate,"d/MM/yyyy");
                    if (dateDue == null) errorMsg = "Specify a valid due date";
                    
                }
            }
            if (errorMsg == null && (daysBefore < 0 || daysBefore > 365)) {
                errorMsg = "Days before must be an integer in the range 0-365";
            }

            return errorMsg;
        }      
    }
}
