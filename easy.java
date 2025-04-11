// 1.course.java
public class Course {
    private String courseName;

    public Course(String courseName) {
        this.courseName = courseName;
    }

    public void displayCourse() {
        System.out.println("Enrolled in course: " + courseName);
    }
}

//2. Student.java
public class Student {
    private Course course;

    // Constructor-based Dependency Injection
    public Student(Course course) {
        this.course = course;
    }

    public void showStudentInfo() {
        System.out.println("Student details:");
        course.displayCourse();
    }
}

//3. AppConfig.java (Java-based configuration using @Configuration)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Course course() {
        return new Course("Spring Framework");
    }

    @Bean
    public Student student() {
        return new Student(course());
    }
}

//4. Main.java (to run the Spring application)
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Student student = context.getBean(Student.class);
        student.showStudentInfo();
    }
}
