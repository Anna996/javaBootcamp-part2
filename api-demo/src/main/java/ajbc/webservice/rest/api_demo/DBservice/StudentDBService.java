package ajbc.webservice.rest.api_demo.DBservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ajbc.webservice.rest.api_demo.DB.MyDB;
import ajbc.webservice.rest.api_demo.models.Student;

public class StudentDBService {

	private MyDB db;
	private Map<Long, Student> students;

	public StudentDBService() {
		db = MyDB.getInstance();
		students = db.getStudents();
	}

	// returns all the students from the DB as a list

	public List<Student> getAllStudents() {
		return new ArrayList<Student>(students.values());
	}

	public Student getStudentByID(long id) {
		return students.get(id);
	}

	public Student addStudent(Student student) {
		if (students.containsKey(student.getID()))
			return null;

		students.put(student.getID(), student);
		return student;
	}

	public Student updateStudent(long id, Student studentDetails) {

		if (students.containsKey(id)) {
			Student currStudent = students.get(id);
			currStudent.setAverage(studentDetails.getAverage());
			currStudent.setFirstName(studentDetails.getFirstName());
			currStudent.setLastName(studentDetails.getLastName());

			students.put(id, currStudent);
			return currStudent;
		}

		return null;
	}

	public Student deleteStudent(long id) {
		return students.remove(id);
	}

	public List<Student> getStudentsByAverage(double average) {
		return students.values().stream().filter(s -> s.getAverage() > average).collect(Collectors.toList());
	}

	public List<Student> getStudentsByAverage(double minAverage, double maxAverage) {
		return students.values().stream().filter(s -> s.getAverage() >= minAverage && s.getAverage() <= maxAverage)
				.collect(Collectors.toList());
	}
}
