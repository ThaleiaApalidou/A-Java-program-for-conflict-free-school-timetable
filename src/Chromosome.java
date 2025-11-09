import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome>
{

    Lesson_Teacher[][][] genes;//[class][day][slot]->lesson_teacher
    private int fitness;
    HashMap<String, Lesson> lessonsMap;
    HashMap<String, Teacher> teachersMap;
    int grade = 9;
    int day = 5;
    int slot = 7;
    int indexA = 0;
    int indexB = 3;
    int indexC = 6;
    String[] grades = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"};
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    String[] slots = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};


    Chromosome(HashMap<String, Lesson> lessonsMap, HashMap<String, Teacher> teachersMap)
    {
        this.lessonsMap = lessonsMap;
        this.teachersMap = teachersMap;

        this.genes = new Lesson_Teacher[grade][day][slot];
        Random r = new Random();
        for(int g = 0; g < grade; g++) {

            ArrayList<Lesson> lessonsList = new ArrayList<>();

            char gr = ' ';
            if(g<indexB){gr='A';}else if(g<indexC){gr='B';}else{gr='C';}

            for(String lessonId:lessonsMap.keySet()){
                if(lessonId.charAt(0)==gr){
                    lessonsList.add(lessonsMap.get(lessonId));
                }
            }


            for(int d = 0; d < day; d++){

                for(int s = 0; s < slot; s++){

                    Lesson randomLesson = lessonsList.get(r.nextInt(lessonsList.size()));

                    ArrayList<Teacher> teachersList = new ArrayList<>();

                    for(Teacher teacher:teachersMap.values()){

                        for (String sub : teacher.getSubjects()){
                            if(sub.equals(randomLesson.getId().substring(2))){
                                teachersList.add(teacher);
                            }
                        }
                    }

                    Teacher randomTeacher = teachersList.get(r.nextInt(teachersList.size()));

                    Lesson_Teacher lesson_teacher = new Lesson_Teacher(randomLesson, randomTeacher);
                    genes[g][d][s] = lesson_teacher;
                }
            }
        }
        this.calculateFitness();
    }


    Chromosome(Lesson_Teacher[][][] genes, HashMap<String, Lesson> lessonsMap, HashMap<String, Teacher> teachersMap)
    {
        this.lessonsMap = lessonsMap;
        this.teachersMap = teachersMap;

        this.genes = new Lesson_Teacher[grade][day][slot];
        for(int g = 0; g < grade; g++) {
            for(int d = 0; d < day; d++){
                for(int s = 0; s < slot; s++){
                    this.genes[g][d][s] = genes[g][d][s];
                }
            }
        }
        this.calculateFitness();
    }


    void calculateFitness()
    {
        //TODO set restrictions and count fitness

        int rightHoursSubjects = 0;
        int TeachersWithRightHours = 0;
        int kenoEndOfDay = 0;
        int equalDistributionForSubject = 0;//TODO
        int equalDistributionForTeachers = 0;//TODO

        HashMap<String, Integer> lessonHourMap = new HashMap<>();
        for(String lessonId:lessonsMap.keySet()){
            lessonHourMap.put(lessonId, 0);
        }

        HashMap<String, Integer> teacherHourMap = new HashMap<>();
        for(String teacherId:teachersMap.keySet()){
            teacherHourMap.put(teacherId, 0);
        }

        for(int g = 0; g < grade; g++) {
            for(int d = 0; d < day; d++){
                for(int s = 0; s < slot; s++){

                    String lessonId = genes[g][d][s].getLesson().getId();
                    lessonHourMap.put(lessonId, lessonHourMap.getOrDefault(lessonId, 0) + 1);

                    String teacherId = genes[g][d][s].getTeacher().getId();
                    teacherHourMap.put(teacherId, teacherHourMap.getOrDefault(teacherId, 0) + 1);

                    if(lessonId.equals("KEN") && s==(slot-1)){
                        kenoEndOfDay++;
                    }

                }
            }
        }

        for(String key:lessonHourMap.keySet()){

            int supposedHours = lessonsMap.get(key).getHours();
            int actualHours = lessonHourMap.get(key);
            rightHoursSubjects =+ supposedHours-actualHours;
        }

        for(String key:teacherHourMap.keySet()){

            int supposedHours = teachersMap.get(key).getHours_weekly();
            int actualHours = teacherHourMap.get(key);
            if(actualHours<=supposedHours){
                TeachersWithRightHours++;
            }
        }

        //System.out.println(this.getFitness());

        this.fitness = rightHoursSubjects*50 + TeachersWithRightHours*50 + kenoEndOfDay*30 + equalDistributionForSubject*10 + equalDistributionForTeachers*10;
    }


    void mutate()
    {
        //swap teacher of a lesson
        Random r = new Random();
        int randomGrade = r.nextInt(grade);
        int randomDay = r.nextInt(day);
        int randomSlot = r.nextInt(slot);

        Lesson lesson = genes[randomGrade][randomDay][randomSlot].getLesson();

        ArrayList<Teacher> teachersList = new ArrayList<>();
        //teachersList.clear();

        for(Teacher t:teachersMap.values()){
            for (String sub : t.getSubjects()){
                if(sub.equals(lesson.getId().substring(2))){
                    teachersList.add(t);
                }
            }
        }
        Teacher teacher = teachersList.get(r.nextInt(teachersList.size()));
        genes[randomGrade][randomDay][randomSlot].setTeacher(teacher);

        //TODO maybe more mutations

        this.calculateFitness();
    }

    public Lesson_Teacher[][][] getGenes() {
        return this.genes;
    }

    public void setGenes(Lesson_Teacher[][][] genes) {
        this.genes = genes;
    }

    public int getFitness() {
        return this.fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    void print(){

        HashMap<String, Lesson[][]> teacherMapWeek = new HashMap<>();

        for (String teacherId : teachersMap.keySet()) {

            Lesson[][] day_slot = new Lesson[day][slot];
            teacherMapWeek.put(teacherId, day_slot);

        }

        for(int g = 0; g < grade; g++) {
            for(int d = 0; d < day; d++){
                for(int s = 0; s < slot; s++){
                    this.genes[g][d][s] = genes[g][d][s];

                    for(String teacherId : teachersMap.keySet()) {

                        if(genes[g][d][s].getTeacher().getId().equals(teacherId)){
                            Lesson[][] day_slot = teacherMapWeek.get(teacherId);
                            day_slot[d][s] = genes[g][d][s].getLesson();
                            teacherMapWeek.put(teacherId, day_slot);
                        }

                    }
                }
            }
        }


        System.out.println("\n * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *Chromosome* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");

        //print days
        System.out.print("    ");
        for(int i = 0; i < 5; i++){
            //System.out.print(" ".repeat(13));
            //System.out.print(" ".repeat(13));
            System.out.print("|             " + days[i] + "             ");
        }
        System.out.print("|");

        //print slots
        System.out.print("\n    | ");
        for(int i=0;i<5;i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(" " + (j+1) + "  ");
            }
            System.out.print("| ");
        }
        System.out.println();

        //go through teachers HashMap
        for (String teacherId : teacherMapWeek.keySet()) {

            Lesson[][] day_slot = teacherMapWeek.get(teacherId);

            //print separator
            System.out.print("----|");
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    System.out.print("----");
                }
                System.out.print("-|");
            }
            System.out.println();

            //print teacher id
            System.out.print(teacherId + " |");
            //print the teacher's lesson for the week
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    Lesson lesson = day_slot[i][j];
                    if(lesson!=null){System.out.print(" " + lesson.getId().substring(2));}else{System.out.print(" ---");}
                }
                System.out.print(" |");
            }
            System.out.println();

            //print the grade that corresponds to the lesson above
            System.out.print("    |");
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    Lesson lesson = day_slot[i][j];
                    if(lesson!=null){System.out.print("  " + lesson.getGrade() + " ");}else{System.out.print("  - ");}
                }
                System.out.print(" |");
            }
            System.out.println();
        }

        //print the last separator
        System.out.print("-".repeat(155));
        System.out.println();

    }

    @Override
    public int compareTo(Chromosome x)
    {
        return this.fitness - x.fitness;
    }



/*

    HashMap<String, String[][]> genesC;//key:class, value:2array[days][slots]->lesson,teacher
    HashMap<Teacher, Lesson[][]> genesT;
    HashMap<String, Teacher> teachers;
    String[][] timetable;
    String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    String[] slots = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};

    Chromosome(){

        this.builtTimetable();
        this.builtGenesPerClass();

    }

    Chromosome(HashMap<String, Teacher> teachers){

        this.teachers = teachers;
        builtGenesPerTeachers(teachers);

    }

    void builtGenesPerTeachers(HashMap<String, Teacher> teachers){

        HashMap<Teacher, Lesson[][]> genes = new HashMap<>();
        Lesson[][] timetable = new Lesson[5][7]; // 5 days, 7 time slots

        for(String i: teachers.keySet()){

            genes.put(teachers.get(i), timetable);

        }
        this.genesT = genes;
    }

    void printGenesPerTeachers(){

        System.out.println("\n * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *Chromosome* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");

        //print days
        System.out.print("    ");
        for(int i = 0; i < 5; i++){
            //System.out.print(" ".repeat(13));
            //System.out.print(" ".repeat(13));
            System.out.print("|             " + days[i] + "             ");
        }
        System.out.print("|");

        //print slots
        System.out.print("\n    | ");
        for(int i=0;i<5;i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print(" " + (j+1) + "  ");
            }
            System.out.print("| ");
        }
        System.out.println();

        //go through teachers HashMap
        for (Teacher teacher : genesT.keySet()) {

            Lesson[][] day_slot = genesT.get(teacher);

            //print separator
            System.out.print("----|");
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    System.out.print("----");
                }
                System.out.print("-|");
            }
            System.out.println();

            //print teacher id
            System.out.print(teacher.id + " |");
            //print the teacher's lesson for the week
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    Lesson lesson = day_slot[i][j];
                    if(lesson!=null){System.out.print(" " + lesson.id);}else{System.out.print(" ---");}
                }
                System.out.print(" |");
            }
            System.out.println();

            //print the grade that corresponds to the lesson above
            System.out.print("    |");
            for (int i = 0; i < day_slot.length; i++) {
                for (int j = 0; j < day_slot[i].length; j++) {
                    Lesson lesson = day_slot[i][j];
                    if(lesson!=null){System.out.print(" " + lesson.grade);}else{System.out.print("  --");}
                }
                System.out.print(" |");
            }
            System.out.println();
        }

        //print the last separator
        System.out.print("-".repeat(155));
        System.out.println();

    }

    //timetable per class
    void builtGenesPerClass(){

        HashMap<String, String[][]> genes = new HashMap<>();

        String[] classes = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"};
        String[][] timetable = new String[7][5]; // 7 time slots, 5 days
        for(String i: classes){

            genes.put(i, timetable);

        }
        this.genesC = genes;
    }

    void printEachClass(){

        System.out.println("\n*********Chromosome*********");

        for (String k : genesC.keySet()) {
            System.out.println("\n"+ "            " + k);
            String[][] tt = genesC.get(k);
            System.out.print("    ");
            for(String d : days){
                System.out.print(d + " ");
            }
            System.out.println();
            for (int i = 0; i < tt.length; i++) {
                System.out.print(slots[i]);
                for (int j = 0; j < tt[i].length; j++) {
                    System.out.print("  - ");
                }
                System.out.println();
            }
        }
    }

    //temp
    void builtTimetable(){

        String[][] timetable = new String[7][5];

        for (int i = 0; i < timetable.length; i++) {
            for (int j = 0; j < timetable[i].length; j++) {
                timetable[i][j] = days[j] + ":" + slots[i];  // Example: Monday:1st, Tuesday:2nd, etc.
            }
        }
        this.timetable = timetable;
    }

    void printTimeTable(){

        for (int i = 0; i < timetable.length; i++) {
            for (int j = 0; j < timetable[i].length; j++) {
                System.out.print(timetable[i][j] + "   ");  // Print slot details
            }
            System.out.println();  // Move to the next line after each row (day)
        }

    }

 */
}

