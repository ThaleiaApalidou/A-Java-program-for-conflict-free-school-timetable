public class Lesson_Teacher {

    private Lesson lesson;
    private Teacher teacher;

    Lesson_Teacher(Lesson lesson, Teacher teacher){

        this.lesson = lesson;
        this.teacher = teacher;

    }

    Lesson getLesson(){return lesson;}
    Teacher getTeacher(){return teacher;}

    void setLesson(Lesson lesson){this.lesson = lesson;}
    void setTeacher(Teacher teacher){this.teacher = teacher;}

}
