package adjb.learn.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@WebServlet("/hello")
@Controller
public class HelloServlet extends HttpServlet{

//	private static final long serialVersionUID = 1L;
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		PrintWriter out = resp.getWriter();
//		out.println("hello from Anna");
//	}
	
	@RequestMapping("/sayhello")
	public String sayHello() {
		System.out.println("Hello");
		return "Hello from servlet";
	}
}