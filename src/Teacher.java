import java.util.ArrayList;

public class Teacher {

    private String id;
    private String name;
    private int hours_weekly;
    private ArrayList<String> subjects;
    //int hours_daily;

    Teacher(String id, String name, int hours_weekly, ArrayList<String> subjects){
        this.id = id;
        this.name = name;
        this.hours_weekly = hours_weekly;
        this.subjects = subjects;
    }

    String getId(){return id;}
    String getName(){return name;}
    int getHours_weekly(){return hours_weekly;}
    ArrayList<String> getSubjects(){return subjects;}

    void printTeacher(){
        System.out.println("\nid: " + id + "\nname: " + name + "\nhours/week: " + hours_weekly);
        System.out.println("subjects:");
        for(int i=0;i<subjects.size();i++){
            System.out.println("  " + subjects.get(i));
        }
    }

}
