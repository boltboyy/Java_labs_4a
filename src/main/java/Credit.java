public class Credit {
    private String subject;
    private boolean passed;

    public Credit(String subject, boolean passed){
        this.subject = subject;
        this.passed = passed;
    }

    public String getSubject(){
        return subject;
    }

    public boolean isPassed(){
        return passed;
    }

    @Override
    public String toString(){
        if(passed) {
            return subject + " - зачтено";
        }
        else {
            return subject + " - не зачтено";
        }
    }
}
