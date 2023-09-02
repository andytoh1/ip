import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class Chatbot {

    public static class Task {
        private String description;
        private boolean done;

        public Task(String description) {
            this.description = description;
            this.done = false;
        }

        public void markAsDone() {
            this.done = true;
        }

        public void markAsNotDone() {
            this.done = false;
        }

        public String getDescription() {
            return description;
        }

        public boolean isDone() {
            return done;
        }
    }

    public static class Todo extends Task {
        public Todo(String description) {
            super(description);
        }
    }

    public static class Deadline extends Task {
        private LocalDateTime deadline;

        String datePattern = "MMM-dd-yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        public Deadline(String description, LocalDateTime deadline) {
            super(description);
            this.deadline = deadline;
        }

        public String getDescription() {
            return super.description + " (by: " + deadline.format(formatter) + ")";
        }

        public String getDeadline() {
            return deadline.format(formatter);
        }

        public String getDescriptionWithoutTime() {
            return super.description;
        }
    }

    public static class Event extends Task {
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        String datePattern = "MMM-dd-yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        public Event(String description, LocalDateTime startTime, LocalDateTime endTime) {
            super(description);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getDescription() {
            String datePattern = "MMM-dd-yyyy HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
            return super.description + " (from: " + startTime.format(formatter) + " to: "
                    + endTime.format(formatter) + ")";
        }

        public String getStartTime() {
            return startTime.format(formatter);
        }

        public String getEndTime() {
            return endTime.format(formatter);
        }

        public String getDescriptionWithoutTime() {
            return super.description;
        }
    }

    public static void main(String[] args) {
        Task[] taskList = new Task[100];
        int taskCount = 0;
        String datePattern = "dd-MM-yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        String datePattern2 = "MMM-dd-yyyy HH:mm";
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(datePattern2);
        try {
            File taskFile = new File("./tasks.txt");
            Scanner taskReader = new Scanner(taskFile);
            while (taskReader.hasNextLine()) {
                String taskFromFile = taskReader.nextLine();
                if (taskFromFile.charAt(0) == 'T') {
                    Todo todo = new Todo(taskFromFile.substring(3));
                    if (taskFromFile.charAt(1) == 't') {
                        todo.markAsDone();
                    }
                    taskList[taskCount] = todo;
                }
                if (taskFromFile.charAt(0) == 'D') {
                    String deadlineFromFile = taskReader.nextLine();
                    LocalDateTime taskDeadline = LocalDateTime.parse(deadlineFromFile, formatter2);
                    Deadline deadline = new Deadline(taskFromFile.substring(3), taskDeadline);
                    if (taskFromFile.charAt(1) == 't') {
                        deadline.markAsDone();
                    }
                    taskList[taskCount] = deadline;
                }
                if (taskFromFile.charAt(0) == 'E') {
                    String startFromFile = taskReader.nextLine();
                    LocalDateTime start = LocalDateTime.parse(startFromFile, formatter2);
                    String endFromFile = taskReader.nextLine();
                    LocalDateTime end = LocalDateTime.parse(endFromFile, formatter2);
                    Event event = new Event(taskFromFile.substring(3), start, end);
                    if (taskFromFile.charAt(1) == 't') {
                        event.markAsDone();
                    }
                    taskList[taskCount] = event;
                }
                taskCount++;
            }
        } catch (FileNotFoundException e) {
            File taskFile = new File("./tasks.txt");
            try {
                taskFile.createNewFile();
            } catch (IOException e2) {
                System.out.println("Error!");
            }
        }
        Scanner userInput = new Scanner(System.in);
        System.out.println("Hello! I'm Chatbot!");
        System.out.println("What can I do for you?");
        while (true) {
            String userMessage = userInput.nextLine();
            if (userMessage.equalsIgnoreCase("bye")) break;
            if (userMessage.equalsIgnoreCase("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    int listNumber = i + 1;
                    System.out.print(listNumber + ". ");
                    if (taskList[i] instanceof Todo) {
                        System.out.print("[T]");
                    } else if (taskList[i] instanceof Deadline) {
                        System.out.print("[D]");
                    } else if (taskList[i] instanceof Event) {
                        System.out.print("[E]");
                    }
                    if (taskList[i].isDone()) System.out.print("[X] ");
                    else System.out.print("[ ] ");
                    System.out.println(taskList[i].getDescription());
                }
                continue;
            }
            try {
                if (userMessage.substring(0, 6).equalsIgnoreCase("delete")) {
                    try {
                        int removeTask = Integer.parseInt(userMessage.substring(7)) - 1;
                        System.out.println("This task will be removed!");
                        if (taskList[removeTask] instanceof Todo) {
                            System.out.print("[T]");
                        } else if (taskList[removeTask] instanceof Deadline) {
                            System.out.print("[D]");
                        } else if (taskList[removeTask] instanceof Event) {
                            System.out.print("[E]");
                        }
                        if (taskList[removeTask].isDone()) System.out.print("[X] ");
                        else System.out.print("[ ] ");
                        System.out.println(taskList[removeTask].getDescription());
                        for (int i = removeTask; i < taskCount - 1; i++) {
                            taskList[i] = taskList[i + 1];
                        }
                        taskCount--;
                        continue;
                    } catch (Exception e) {
                        System.out.println("Please use the format \"delete <task number>\"!");
                        continue;
                    }
                } else if (userMessage.substring(0, 4).equalsIgnoreCase("mark")) {
                    try {
                        int doneTask = Integer.parseInt(userMessage.substring(5)) - 1;
                        System.out.println("Well done! This task has been marked as done.");
                        if (taskList[doneTask] instanceof Todo) {
                            System.out.print("[T]");
                        } else if (taskList[doneTask] instanceof Deadline) {
                            System.out.print("[D]");
                        } else if (taskList[doneTask] instanceof Event) {
                            System.out.print("[E]");
                        }
                        System.out.print("[X] ");
                        System.out.println(taskList[doneTask].getDescription());
                        taskList[doneTask].markAsDone();
                        continue;
                    } catch (Exception e) {
                        System.out.println("Please use the format \"mark <task number>\"!");
                        continue;
                    }
                } else if (userMessage.substring(0, 4).equalsIgnoreCase("todo")) {
                    try {
                        Todo todo = new Todo(userMessage.substring(5));
                        taskList[taskCount] = todo;
                    } catch (Exception e) {
                        System.out.println("Please use the format \"todo <task description>\"!");
                        continue;
                    }
                } else if (userMessage.substring(0, 8).equalsIgnoreCase("deadline")) {
                    try {
                        int index = userMessage.indexOf("/by");
                        String description = userMessage.substring(9, index - 1);
                        LocalDateTime taskDeadline = LocalDateTime.parse(userMessage.substring(index + 4), formatter);
                        Deadline deadline = new Deadline(description, taskDeadline);
                        taskList[taskCount] = deadline;
                    } catch (Exception e) {
                        System.out.println("Please use the format \"deadline <task description> /by DD-MM-YYYY HH:MM\"!");
                        continue;
                    }
                } else if (userMessage.substring(0, 5).equalsIgnoreCase("event")) {
                    try {
                        int index = userMessage.indexOf("/from");
                        int index2 = userMessage.indexOf("/to");
                        String description = userMessage.substring(6, index - 1);
                        String start = userMessage.substring(index + 6, index2 - 1);
                        String end = userMessage.substring(index2 + 4);
                        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
                        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
                        Event event = new Event(description, startTime, endTime);
                        taskList[taskCount] = event;
                    } catch (Exception e) {
                        System.out.println("Please use the format \"event <task description> /from DD-MM-YYYY HH:MM "
                                + "/to DD-MM-YYYY HH:MM\"!");
                        continue;
                    }
                } else {
                    System.out.println("I'm sorry, but I don't know what that means!");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("I'm sorry, but I don't know what that means!");
                continue;
            }
            System.out.print("Added this task: ");
            if (taskList[taskCount] instanceof Todo) {
                System.out.print("[T] ");
            } else if (taskList[taskCount] instanceof Deadline) {
                System.out.print("[D] ");
            } else if (taskList[taskCount] instanceof Event) {
                System.out.print("[E] ");
            }
            System.out.println(taskList[taskCount].getDescription());
            try {
                FileWriter taskWriter = new FileWriter("./tasks.txt", false);
                for (int taskNumber = 0; taskNumber <= taskCount; taskNumber++) {
                    if (taskList[taskNumber] instanceof Todo) {
                        taskWriter.write("T");
                        taskWriter.write(taskList[taskNumber].isDone() ? "t " : "f ");
                        taskWriter.write(taskList[taskNumber].getDescription() + "\n");
                    } else if (taskList[taskNumber] instanceof Deadline) {
                        taskWriter.write("D");
                        taskWriter.write(taskList[taskNumber].isDone() ? "t " : "f ");
                        taskWriter.write(((Deadline)taskList[taskNumber]).getDescriptionWithoutTime() + "\n");
                    } else if (taskList[taskNumber] instanceof Event) {
                        taskWriter.write("E");
                        taskWriter.write(taskList[taskNumber].isDone() ? "t " : "f ");
                        taskWriter.write(((Event)taskList[taskNumber]).getDescriptionWithoutTime() + "\n");
                    }
                    if (taskList[taskNumber] instanceof Deadline) {
                        taskWriter.write(((Deadline) taskList[taskNumber]).getDeadline() + "\n");
                    }
                    if (taskList[taskNumber] instanceof Event) {
                        taskWriter.write(((Event) taskList[taskNumber]).getStartTime() + "\n");
                        taskWriter.write(((Event) taskList[taskNumber]).getEndTime() + "\n");
                    }
                    taskWriter.flush();
                }
            } catch (Exception e) {
                System.out.println("Error!");
            }
            taskCount++;
        }
        System.out.println("Bye. Hope to see you again soon!");
    }
}
