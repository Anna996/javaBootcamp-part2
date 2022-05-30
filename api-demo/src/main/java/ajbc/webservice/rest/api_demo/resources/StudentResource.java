package ajbc.webservice.rest.api_demo.resources;

import java.net.URI;
import java.util.List;

import ajbc.webservice.rest.api_demo.DBservice.StudentDBService;
import ajbc.webservice.rest.api_demo.beans.StudentFilterBean;
import ajbc.webservice.rest.api_demo.models.Course;
import ajbc.webservice.rest.api_demo.models.Student;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {
	StudentDBService studentDB = new StudentDBService();

//	@GET
//	public List<Student> getAllStudents() {
//		return studentDB.getAllStudents();
//	}

//	@GET
//	public List<Student> getStudentsByAverage(@QueryParam("average") double average) {
//		if (average > 0)
//			return studentDB.getStudentsByAverage(average);
//		return studentDB.getAllStudents();
//	}
//	
//	@GET
//	public List<Student> getStudentsByAverageRange(@QueryParam("average") double average, @QueryParam("minAverage") double minAverage,@QueryParam("maxAverage") double maxAverage) {
//		if (average > 0)
//			return studentDB.getStudentsByAverage(average);
//		if (minAverage > 0 && maxAverage > 0)
//			return studentDB.getStudentsByAverage(minAverage, maxAverage);
//		return studentDB.getAllStudents();
//	}

	@GET
	public List<Student> getStudentsByAverageRange(@BeanParam StudentFilterBean studentFilter) {
		if (studentFilter.getAverage() > 0)
			return studentDB.getStudentsByAverage(studentFilter.getAverage());
		if (studentFilter.getMinAverage() > 0 && studentFilter.getMaxAverage() > 0)
			return studentDB.getStudentsByAverage(studentFilter.getMinAverage(), studentFilter.getMaxAverage());
		return studentDB.getAllStudents();
	}

	@GET
	@Path("/{id}")
	public Student getStudent(@PathParam("id") long id) {
		return studentDB.getStudentByID(id);
	}

	@GET
	@Path("/{id}/courses")
	public List<Course> getStudentCourses(@PathParam("id") long id) {
		Student student = studentDB.getStudentByID(id);
		return student == null ? null : student.getCourses();
	}

	@GET
	@Path("/{id}/courses/{course_number}")
	public Course getStudentCourse(@PathParam("id") long id, @PathParam("course_number") long number) {
		List<Course> courses = this.getStudentCourses(id);

		if (courses == null)
			return null;

		for (Course course : courses) {
			if (course.getNUMBER() == number) {
				return course;
			}
		}

		return null;
	}

//	@POST
//	public Student addStudent(Student student) {
//		return studentDB.addStudent(student);
//	}

	@POST
	public Response addStudent(Student student, @Context UriInfo uriInfo) {
		Student currStudent = studentDB.addStudent(student);
		Status status;
		URI uri = null;

		if (currStudent == null)
			status = Status.NOT_MODIFIED;
		else {
			status = Status.CREATED;
			uri = uriInfo.getAbsolutePathBuilder().path("" + currStudent.getID()).build();
		}

		return Response.status(status).entity(currStudent).location(uri).build();
	}

	@PUT
	@Path("/{id}")
	public Student updateStudent(@PathParam("id") long id, Student student) {
		return studentDB.updateStudent(id, student);
	}

	@DELETE
	@Path("/{id}")
	public Student deleteStudent(@PathParam("id") long id) {
		return studentDB.deleteStudent(id);
	}
}