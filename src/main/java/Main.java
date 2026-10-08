import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;

public class Main {
    public static void loadFile(String fileName, HashMap<String, GradeBook> books) {
        try (BufferedReader br = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8)) {
            String line;
            String header = br.readLine();
            if (header == null) {
                System.out.println("Файл пуст: " + fileName);
                return;
            }
            String[] h = header.split(";");
            if (h.length < 4) {
                System.out.println("Неверная шапка в файле: " + fileName);
                return;
            }
            String subject = h[0].trim();
            String type = h[1].trim();
            int sessionNumber;
            int course;
            try {
                sessionNumber = Integer.parseInt(h[2].trim());
                course = Integer.parseInt(h[3].trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный номер сессии или курса в файле: " + fileName);
                return;
            }
            if (sessionNumber < 1 || sessionNumber > 9) {
                System.out.println("Номер сессии вне диапазона 1-9: " + fileName);
                return;
            }
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length < 7) {
                    System.out.println("Пропущена строка (мало полей): " + line);
                    continue;
                }
                String number = parts[0].trim();
                String surname = parts[1].trim();
                String name = parts[2].trim();
                String patronymic = parts[3].trim();
                String group = parts[4].trim();
                String faculty = parts[5].trim();
                GradeBook book = books.get(number);
                if (book == null) {
                    book = new GradeBook(number, surname, name, patronymic, group, faculty);
                    books.put(number, book);
                }
                GradeBook.Session session = book.getOrCreateSession(sessionNumber, course);
                if ("экзамен".equals(type)) {
                    int mark;
                    try {
                        mark = Integer.parseInt(parts[6].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Неверная оценка в строке: " + line);
                        continue;
                    }
                    if (mark < 1 || mark > 10) {
                        System.out.println("Оценка вне диапазона 1-10: " + line);
                        continue;
                    }
                    session.addExam(subject, mark);
                } else if ("зачет".equals(type)) {
                    boolean passed = "зачтено".equals(parts[6].trim());
                    session.addCredit(subject, passed);
                } else {
                    System.out.println("Неизвестный тип контроля: " + type);
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("ошибка чтения файла " + e.getMessage());
        }
    }

    public static void writeExcellent(String fileName, HashMap<String, GradeBook> books) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Path.of(fileName), StandardCharsets.UTF_8))) {
            for (GradeBook b : books.values()) {
                if (b.allMarks(9)) {
                    out.println(b.getSurname() + " " + b.getName() + " " + b.getPatronymic() + ", группа " + b.getGroup());
                    for (GradeBook.Session s : b.getSessions()) {
                        out.println("Сессия " + s.getNumber() + ", курс " + s.getCourse());
                        for (Exam e : s.getExams()) {
                            out.println("    " + e.getSubject() + " - " + e.getMark());
                        }
                        for (Credit c : s.getCredits()) {
                            out.println("    " + c.getSubject() + " - зачтено");
                        }
                    }
                    out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи: " + e.getMessage());
        }
    }

    public static void writePassing(String fileName, HashMap<String, GradeBook> books) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Path.of(fileName), StandardCharsets.UTF_8))) {
            for (GradeBook b : books.values()) {
                if (b.allMarks(4)) {
                    out.println(b.getSurname() + " " + b.getName() + " " + b.getPatronymic() + ", группа " + b.getGroup());
                    for (GradeBook.Session s : b.getSessions()) {
                        double avg = s.getAverageMark();
                        if (avg < 0) {
                            out.printf("Сессия %d, курс %d: экзаменов нет%n", s.getNumber(), s.getCourse());
                        } else {
                            out.printf("Сессия %d, курс %d: средний балл %.2f%n", s.getNumber(), s.getCourse(), avg);
                        }
                    }
                    out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        HashMap<String, GradeBook> books = new HashMap<>();

        loadFile("exams_s1_math.txt", books);
        loadFile("exams_s1_physics.txt", books);
        loadFile("credits_s1_pe.txt", books);
        loadFile("exams_s2_math.txt", books);
        loadFile("exams_s2_informatics.txt", books);
        loadFile("credits_s2_pe.txt", books);
        loadFile("broken_test.txt", books);

        writeExcellent("output1_excellent.txt", books);
        writePassing("output2_passing.txt", books);

        for (GradeBook b : books.values()) {
            if (b.allMarks(9)) {
                System.out.println(b.getSurname() + " - отличник");
                for (GradeBook.Session s : b.getSessions()) {
                    System.out.printf("Сессия %d: средний балл %.2f%n", s.getNumber(), s.getAverageMark());
                }
            } else {
                if (b.allMarks(4)) {
                    System.out.println(b.getSurname() + " - сдал");
                    for (GradeBook.Session s : b.getSessions()) {
                        System.out.printf("Сессия %d: средний балл %.2f%n", s.getNumber(), s.getAverageMark());
                    }
                }
            }
        }

        ObjectMapper oMap = new ObjectMapper();
        try {
            oMap.writerWithDefaultPrettyPrinter().writeValue(new File("gradebook.json"), new ArrayList<>(books.values()));
        } catch (IOException e)
        {
            System.out.println("Ошибка записи: " + e.getMessage());
        }
    }
}