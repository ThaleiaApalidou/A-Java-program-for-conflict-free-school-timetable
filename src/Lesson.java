public class Lesson {

    private String id;
    private String subject;
    private String grade;
    private int hours;

    Lesson(String id, String subject, String grade, int hours){
        this.id = id;
        this.subject = subject;
        this.grade = grade;
        this.hours = hours;
    }

    String getId(){return id;}
    String getSubject(){return subject;}
    String getGrade(){return grade;}
    int getHours(){return hours;}

    void printLesson(){
        System.out.println("\nid: " + id + "\nsubject: " + subject + "\ngrade: " + grade + "\nhours: " + hours);
    }

}
