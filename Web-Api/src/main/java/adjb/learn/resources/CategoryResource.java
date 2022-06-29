package adjb.learn.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import adjb.learn.dao.CategoryDao;
import adjb.learn.dao.DaoException;
import adjb.learn.dao.ProductDao;
import adjb.learn.models.Category;
import adjb.learn.models.ErrorMessage;

@RestController
@RequestMapping("/categories")
public class CategoryResource {

	@Autowired
	CategoryDao dao;
	
	@Autowired
	ProductDao productDao;

//	private ErrorMessage getErrorMessage(String data, String message) {
//		ErrorMessage errorMessage = new ErrorMessage();
//		errorMessage.setData(data);
//		errorMessage.setMessage(message);
//		return errorMessage;
//	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Category>> getAllCategories() throws DaoException {
		List<Category> categories = dao.getAllCategories();

		if (categories == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		
		return ResponseEntity.ok(categories);
	}

	@RequestMapping(method = RequestMethod.GET, path="/{id}")
	public ResponseEntity<?> getCategory(@PathVariable int id) {
		try {
			Category category = dao.getCategory(id);
			return ResponseEntity.ok(category);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "category id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addCategory(@RequestBody Category category) {
		try {
			dao.addCategory(category);
			category = dao.getCategory(category.getCategoryId());
			return ResponseEntity.status(HttpStatus.CREATED).body(category);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "faild to add this category to DB");
			return ResponseEntity.status(500).body(eMessage);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, path="/{id}")
	public ResponseEntity<?> updateCategory(@RequestBody Category category, @PathVariable int id) {
		
		try {
			category.setCategoryId(id);
			dao.updateCategory(category);
			category = dao.getCategory(id);
			return ResponseEntity.ok(category);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "faild to update this category in DB");
			return ResponseEntity.status(404).body(eMessage);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, path="/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable int id) {
		try {
			dao.deleteCategory(id);
			Category category = dao.getCategory(id);
			productDao.deleteCategory(id);
			return ResponseEntity.ok(category);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "faild to delete this category from DB");
			return ResponseEntity.status(404).body(eMessage);
		}
	}
}
