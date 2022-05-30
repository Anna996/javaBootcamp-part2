package ajbc.webservice.rest.api_demo.resources;

import java.util.List;

import ajbc.webservice.rest.api_demo.DBservice.CourseDBService;
import ajbc.webservice.rest.api_demo.DBservice.StudentDBService;
import ajbc.webservice.rest.api_demo.models.Course;
import ajbc.webservice.rest.api_demo.models.Student;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("courses")
public class CourseResource {
	CourseDBService courseDB = new CourseDBService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getAllCourses() {
		return courseDB.getAllCourses();
	}

	@GET
	@Path("/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourseByNumber(@PathParam("number") long number) {
		return courseDB.getCoursetByNumber(number);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Course addCourse(Course course) {
		return courseDB.addCourse(course);
	}

	@PUT
	@Path("/{number}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Course updateCourseInfo(@PathParam("number") long number, Course course) {
		return courseDB.updateCourse(number, course);
	}

	@DELETE
	@Path("/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Course deleteCourse(@PathParam("number") long number) {
		return courseDB.deleteCourse(number);
	}
}