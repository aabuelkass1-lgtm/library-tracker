import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LibraryTracker {

    static class Book {
        int id;
        String title;
        String author;

        Book(int id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }
    }

    static final String FILE_NAME = "books.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java LibraryTracker [add <title> <author> | list]");
            return;
        }

        String command = args[0];

        if (command.equals("add")) {
            if (args.length < 3) {
                System.out.println("Please enter both title and author.");
                return;
            }
            addBook(args[1], args[2]);
        } else if (command.equals("list")) {
            listBooks();
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    static ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return books;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("{")) {
                    // Remove { and } and trailing comma
                    line = line.replace("{", "").replace("}", "").trim();
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.length() - 1);
                    }

                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0].split(":")[1].trim());
                    String title = parts[1].split(":")[1].trim().replace("\"", "");
                    String author = parts[2].split(":")[1].trim().replace("\"", "");

                    books.add(new Book(id, title, author));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading books.");
        }
        return books;
    }

    static void saveBooks(ArrayList<Book> books) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("[\n");
            for (int i = 0; i < books.size(); i++) {
                Book b = books.get(i);
                writer.write("  {\"id\": " + b.id + ", \"title\": \"" + b.title + "\", \"author\": \"" + b.author + "\"}");
                if (i < books.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]\n");
        } catch (IOException e) {
            System.out.println("Error saving books.");
        }
    }

    static void addBook(String title, String author) {
        ArrayList<Book> books = loadBooks();
        int newId = books.size() + 1;
        books.add(new Book(newId, title, author));
        saveBooks(books);
        System.out.println("Book added: " + title + " by " + author);
    }

    static void listBooks() {
        ArrayList<Book> books = loadBooks();
        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        for (Book b : books) {
            System.out.println(b.id + ". " + b.title + " - " + b.author);
        }
    }
}