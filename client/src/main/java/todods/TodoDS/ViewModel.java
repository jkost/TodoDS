package todods.TodoDS;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.OnPropertyChange;
import net.java.html.json.Property;
import todods.TodoDS.js.Dialogs;

@Model(className = "TaskList", targetId = "", properties = {
    @Property(name = "selected", type = Task.class),
    @Property(name = "edited", type = Task.class),
    @Property(name = "tasks", type = Task.class, array = true),
    @Property(name = "showCompleted", type = boolean.class),
    @Property(name = "sortByPriority", type = boolean.class)
})
final class ViewModel {
    private static final List<Task> tasksList = new ArrayList<>();

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        Task task = new Task();
        task.setPriority(10);
        task.setDescription("Finish TodoDS article!");
        task.setAlert(true);
        task.setDueDate("10/03/2017");
        task.setCompleted(false);
        TaskList taskList = new TaskList();
        taskList.setSelected(null);
        taskList.setEdited(null);
        taskList.getTasks().add(task);
        taskList.getTasks().add(new Task(2, "Book venue!", 7, "01/04/2017", false, 2, "", false));
        tasksList.addAll(taskList.getTasks());
        taskList.applyBindings();
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
        if (task == null) return;
        final Task selectedTask = tasks.getSelected();
        if (selectedTask != null) {
            tasks.getTasks().set(tasks.getTasks().indexOf(selectedTask), task);
        } else {
            tasks.getTasks().add(task);
        }
        tasksList.add(task);
        tasks.setEdited(null);
    }     
    
    @Function 
    static void cancel(TaskList tasks) {
        tasks.setSelected(null);
        tasks.setEdited(null);
    }   
    
    @Function
    static void removeTask(TaskList tasks, Task data) {
        tasks.getTasks().remove(data);
        tasksList.remove(data);
    } 
    
    @Function 
    static void expiredTasks(final TaskList tasks) {
        Dialogs.showAlerts(listTasksWithAlert(tasks.getTasks()));
    }
    
    private static List<Task> listTasksWithAlert(List<Task> tasks) {
        return tasks.stream().filter(Task::isAlert).collect(toList());
    }    

    @ComputedProperty
    static int numberOfTasksWithAlert(List<Task> tasks) {
        return listTasksWithAlert(tasks).size();
    }     
    
    @OnPropertyChange("showCompleted")
    static void showCompleted(final TaskList tasks) {
        tasks.getTasks().clear();
        if (tasks.isShowCompleted()) {
            tasks.getTasks().addAll(tasksList.stream().filter(Task::isCompleted).collect(toList()));
        } else {
            tasks.getTasks().addAll(tasksList);
        }
    }    
    
    @OnPropertyChange("sortByPriority")
    static void sortBy(final TaskList tasks) {
        tasks.getTasks().sort(tasks.isSortByPriority() ? 
                new PriorityComparator() : new DueDateComparator());
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
            LocalDate t1DateDue = LocalDate.parse(t1.getDueDate(),
                    DateTimeFormatter.ofPattern("d/MM/yyyy"));
            LocalDate t2DateDue = LocalDate.parse(t2.getDueDate(),
                    DateTimeFormatter.ofPattern("d/MM/yyyy"));
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
        @OnPropertyChange("completed")
        static void markAsCompleted(final Task task) {
            task.setCompleted(task.isCompleted());
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
        static boolean hasAlert(String dueDate, boolean alert, int daysBefore) {
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