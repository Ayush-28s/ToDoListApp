import java.io.*;
import java.util.*;

class Task {
    String description;
    boolean isDone;

    Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    Task(String description) {
        this(description, false);
    }

    void markDone() {
        isDone = true;
    }

    public String toString() {
        return (isDone ? "[✓] " : "[ ] ") + description;
    }

    String toFileString() {
        return isDone + ";" + description;
    }

    static Task fromFileString(String line) {
        String[] parts = line.split(";", 2);
        return new Task(parts[1], Boolean.parseBoolean(parts[0]));
    }
}

public class ToDoListApp {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Task> tasks = new ArrayList<>();
    private static String currentUser = "";

    public static void main(String[] args) {
        if (!loginOrRegister()) return;
        loadTasks();

        int choice;
        do {
            showMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: addTask(); break;
                case 2: viewTasks(); break;
                case 3: markTaskDone(); break;
                case 4: deleteTask(); break;
                case 5: saveTasks(); System.out.println("Logged out."); break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    static boolean loginOrRegister() {
        System.out.println("--- Welcome to To-Do App ---");
        System.out.print("1. Login\n2. Register\nChoose: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        File userFile = new File("users.txt");
        try {
            if (option == 1) { // Login
                Scanner fileScanner = new Scanner(userFile);
                while (fileScanner.hasNextLine()) {
                    String[] creds = fileScanner.nextLine().split(":");
                    if (creds.length == 2 && creds[0].equals(username) && creds[1].equals(password)) {
                        currentUser = username;
                        return true;
                    }
                }
                System.out.println("Login failed.");
                return false;
            } else if (option == 2) { // Register
                FileWriter fw = new FileWriter(userFile, true);
                fw.write(username + ":" + password + "\n");
                fw.close();
                currentUser = username;
                return true;
            } else {
                System.out.println("Invalid option.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error handling user file.");
            return false;
        }
    }

    static void loadTasks() {
        tasks.clear();
        File file = new File(currentUser + "_tasks.txt");
        if (!file.exists()) return;
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                tasks.add(Task.fromFileString(fileScanner.nextLine()));
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Error loading tasks.");
        }
    }

    static void saveTasks() {
        try {
            FileWriter writer = new FileWriter(currentUser + "_tasks.txt");
            for (Task t : tasks) {
                writer.write(t.toFileString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks.");
        }
    }

    static void showMenu() {
        System.out.println("\n--- To-Do Menu for " + currentUser + " ---");
        System.out.println("1. Add Task");
        System.out.println("2. View Tasks");
        System.out.println("3. Mark Task as Done");
        System.out.println("4. Delete Task");
        System.out.println("5. Logout");
        System.out.print("Choose: ");
    }

    static void addTask() {
        System.out.print("Enter task: ");
        String desc = scanner.nextLine();
        tasks.add(new Task(desc));
        System.out.println("Task added.");
    }

    static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    static void markTaskDone() {
        viewTasks();
        System.out.print("Enter task number to mark done: ");
        int n = scanner.nextInt();
        if (n >= 1 && n <= tasks.size()) {
            tasks.get(n - 1).markDone();
            System.out.println("Marked as done.");
        } else {
            System.out.println("Invalid task.");
        }
    }

    static void deleteTask() {
        viewTasks();
        System.out.print("Enter task number to delete: ");
        int n = scanner.nextInt();
        if (n >= 1 && n <= tasks.size()) {
            tasks.remove(n - 1);
            System.out.println("Task deleted.");
        } else {
            System.out.println("Invalid task.");
        }
    }
}
