import java.util.ArrayList;

public class GradeBook {
    private String number;
    private String surname;
    private String name;
    private String patronymic;
    private String group;
    private String faculty;

    private ArrayList<Session> sessions = new ArrayList<>();

    public GradeBook(String number, String surname, String name, String patronymic, String group, String faculty) {
        this.number = number;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.group = group;
        this.faculty = faculty;
    }

    public Session addSession(int number, int course) {
        Session s = new Session(number, course);
        sessions.add(s);
        return s;
    }

    public boolean allMarks(int minimum) {
        for (Session s : sessions) {
            if (!s.allMarks(minimum)) return false;
        }
        return true;
    }

    public Session getOrCreateSession(int sessionNumber, int course){
        Session found = null;
        for (Session s: sessions){
            if(s.getNumber() == sessionNumber){
                found = s;
                break;
            }
        }
        if(found == null){
            found = addSession(sessionNumber, course);
        }
        return found;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getGroup() {
        return group;
    }

    public String getNumber() {
        return number;
    }

    public String getFaculty() {return faculty; }

    public ArrayList<Session> getSessions(){
        return sessions;
    }

    @Override
    public String toString() {
        String result = surname + " " + name + " " + patronymic + " группа № " + group + "\n";
        for (Session s : sessions) {
            result = result + s + "\n";
        }
        return result;
    }

    public class Session {
        private int number;
        private int course;

        private ArrayList<Exam> exams = new ArrayList<>();
        private ArrayList<Credit> credits = new ArrayList<>();

        public Session(int number, int course) {
            this.number = number;
            this.course = course;
        }

        public void addExam(String subject, int mark) {
            exams.add(new Exam(subject, mark));
        }

        public void addCredit(String subject, boolean passed) {
            credits.add(new Credit(subject, passed));
        }

        public boolean allMarks(int minimum) {
            for (Exam e : exams) {
                if (e.getMark() < minimum) {
                    return false;
                }
            }
            for (Credit c : credits) {
                if (!c.isPassed()) return false;
            }
            return true;
        }

        public double getAverageMark(){
            if (exams.isEmpty()){
                return -1;
            }
            int sum = 0;
            for(Exam e: exams){
                sum = sum + e.getMark();
            }
            int count = exams.size();
            double average = (double) sum/count;
            return average;
        }

        public int getNumber() {
            return number;
        }

        public int getCourse() {
            return course;
        }

        public ArrayList<Exam> getExams() {
            return exams;
        }

        public ArrayList<Credit> getCredits() {
            return credits;
        }

        @Override
        public String toString() {
            String result = "Сессия " + number + ", курс " + course + "\n";
            for (Exam e : exams) {
                result = result + e + "\n";
            }
            for (Credit c : credits) {
                result = result + c + "\n";
            }
            return result;
        }
    }
}
