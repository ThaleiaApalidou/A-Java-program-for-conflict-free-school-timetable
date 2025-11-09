import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        HashMap<String, Lesson> lessonsMap = readLessonXML();
        HashMap<String, Teacher> teachersMap = readTeachersXML();
        int[] fitness = countMinFitness(lessonsMap, teachersMap);
        int minFitness = fitness[0];
        int totalFitness = fitness[1];

        GeneticAlgorithm algorithm = new GeneticAlgorithm(lessonsMap, teachersMap);
        Chromosome solution = algorithm.run(2000, 0.04, 100, totalFitness, minFitness);

        solution.print();
    }

    static HashMap<String, Lesson> readLessonXML(){

        HashMap<String, Lesson> map = new HashMap<>();

        try {

            File xmlFile = new File("lessons.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList lessons = document.getElementsByTagName("lesson");
            for (int i = 0; i < lessons.getLength(); i++) {
                Node lessonNode = lessons.item(i);

                if (lessonNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element lessonElement = (Element) lessonNode;

                    String id = lessonElement.getElementsByTagName("id").item(0).getTextContent();
                    String subject = lessonElement.getElementsByTagName("subject").item(0).getTextContent();
                    String grade = lessonElement.getElementsByTagName("grade").item(0).getTextContent();
                    String hoursString = lessonElement.getElementsByTagName("hours").item(0).getTextContent();
                    int hours = Integer.parseInt(hoursString);

                    Lesson lesson = new Lesson(id, subject, grade, hours);

                    map.put(id, lesson);
                }
            }

            Lesson lesson = new Lesson("C_KEN", "KENO", "C", 1);
            map.put(lesson.getId(), lesson);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return map;

    }

    static HashMap<String, Teacher> readTeachersXML(){

        HashMap<String, Teacher> map = new HashMap<>();

        try {

            File xmlFile = new File("teachers.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList teachers = document.getElementsByTagName("teacher");
            for (int i = 0; i < teachers.getLength(); i++) {
                Node teacherNode = teachers.item(i);

                if (teacherNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element teacherElement = (Element) teacherNode;

                    String id = teacherElement.getElementsByTagName("id").item(0).getTextContent();
                    String name = teacherElement.getElementsByTagName("name").item(0).getTextContent();
                    String hoursString = teacherElement.getElementsByTagName("hours").item(0).getTextContent();
                    int hours = Integer.parseInt(hoursString);

                    NodeList subjectList = teacherElement.getElementsByTagName("subject");
                    ArrayList<String> subjects = new ArrayList<>();
                    for (int j = 0; j < subjectList.getLength(); j++) {
                        subjects.add(subjectList.item(j).getTextContent().trim());
                    }

                    Teacher teacher = new Teacher(id, name, hours, subjects);

                    map.put(id, teacher);
                }
            }
            ArrayList<String> list = new ArrayList<>();
            list.add("KEN");
            Teacher teacher = new Teacher("ken", "Κενό", 3, list);
            map.put(teacher.getId(), teacher);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return map;

    }

    //temp
    static void printMaps(HashMap<String, Teacher> teacherMap, HashMap<String, Lesson> lessonsMap){
        //printMaps(teacherMap, lessonsMap);
        System.out.println("\nTeachers:");
        teacherMap.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        System.out.println("\nLessons:");
        lessonsMap.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
    }

    private static int[] countMinFitness(HashMap<String, Lesson> lessonsMap, HashMap<String, Teacher> teachersMap){

        int rightHoursSubjects = 0;
        for(Lesson lesson:lessonsMap.values()){
            rightHoursSubjects =+ lesson.getHours();
        }

        int TeachersWithRightHours = teachersMap.size();
        int kenoEndOfDay = 3;
        int equalDistributionForSubject = lessonsMap.size();
        int equalDistributionForTeachers = teachersMap.size();;

        int minFitness = rightHoursSubjects*50 + TeachersWithRightHours*50;
        int totalFitness = rightHoursSubjects*50 + TeachersWithRightHours*50 + kenoEndOfDay*30 + equalDistributionForSubject*10 + equalDistributionForTeachers*10;

        int[] fitness = new int[2];
        fitness[0] = minFitness;
        fitness[1] = totalFitness;

        return fitness;

    }

}

