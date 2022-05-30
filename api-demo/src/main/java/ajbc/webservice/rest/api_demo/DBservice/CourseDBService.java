package ajbc.webservice.rest.api_demo.DBservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ajbc.webservice.rest.api_demo.DB.MyDB;
import ajbc.webservice.rest.api_demo.models.Course;
import ajbc.webservice.rest.api_demo.models.Student;

public class CourseDBService {

	private MyDB db;
	private Map<Long, Course> courses;

	public CourseDBService() {
		db = MyDB.getInstance();
		courses = db.getCourses();
	}

	public List<Course> getAllCourses() {
		return new ArrayList<Course>(courses.values());
	}

	public Course getCoursetByNumber(long number) {
		return courses.get(number);
	}

	public Course addCourse(Course course) {
		courses.put(course.getNUMBER(), course);
		return course;
	}

	public Course updateCourse(long number, Course courseDetails) {

		if (courses.containsKey(number)) {
			Course currCourse = courses.get(number);
			currCourse.setName(courseDetails.getName());
			currCourse.setStudents(courseDetails.getStudents());

			courses.put(number, currCourse);
			return currCourse;
		}

		return null;
	}

	public Course deleteCourse(long number) {
		return courses.remove(number);
	}
}
