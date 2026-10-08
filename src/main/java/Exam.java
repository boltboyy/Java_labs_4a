public class Exam {
    private String subject;
    private int mark;

    public Exam(String subject, int mark) {
        this.subject = subject;
        this.mark = mark;
    }

    public int getMark() {
        return mark;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return subject + " - " + mark;
    }
}
