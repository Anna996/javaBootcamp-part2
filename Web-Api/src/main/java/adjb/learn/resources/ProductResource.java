package adjb.learn.resources;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import adjb.learn.dao.DaoException;
import adjb.learn.dao.ProductDao;
import adjb.learn.models.Category;
import adjb.learn.models.ErrorMessage;
import adjb.learn.models.Product;
import adjb.learn.models.Supplier;

@RequestMapping("/products")
@RestController
public class ProductResource {

	@Autowired
	ProductDao dao;

//	@RequestMapping(method = RequestMethod.GET)
//	public ResponseEntity<List<Product>> getAllProducts() throws DaoException{
//		List<Product> products = dao.getAllProducts();
//		
//		if(products == null) {
//			return ResponseEntity.notFound().build();
//		}
//		
//		return ResponseEntity.ok(products);
//	}

//	@RequestMapping(method = RequestMethod.GET)
//	public ResponseEntity<List<Product>> getProductsByRange(@RequestParam Optional<Double> min, @RequestParam Optional<Double> max) throws DaoException{
//		List<Product> products;
//		if(min.isPresent() && max.isPresent()) {
//			products = dao.getProductsByPriceRange(min.get(), max.get());
//		}
//		else {
//			products = dao.getAllProducts();
//		}
//		
//		if(products == null) {
//			return ResponseEntity.notFound().build();
//		}
//		
//		return ResponseEntity.ok(products);
//	}
	
	// ======================================= GET =======================================

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Product>> getProductsByRange(@RequestParam Map<String, String> map) throws DaoException {
		List<Product> products;
		Set<String> keys = map.keySet();

		if (keys.contains("min") && keys.contains("max")) {
			products = dao.getProductsByPriceRange(Double.valueOf(map.get("min")), Double.valueOf(map.get("max")));
		} else {
			products = dao.getAllProducts();
		}

		if (products == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(products);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public ResponseEntity<?> getProductsById(@PathVariable Integer id) {
		Product product;

		try {
			product = dao.getProduct(id);
			return ResponseEntity.ok(product);

		} catch (DaoException e) {
			ErrorMessage errorMassage = new ErrorMessage();
			errorMassage.setData(e.getMessage());
			errorMassage.setMessage("Failed to get product with id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMassage);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/{id}/category")
	public ResponseEntity<?> getCategoryOfProduct(@PathVariable int id){
		
		try {
			Category category = dao.getCategoryOfProduct(id);
			
			if(category == null) {
				throw new DaoException("There is no category ");
			}
			
			return ResponseEntity.ok(category);
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "product id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/{id}/supplier")
	public ResponseEntity<?> getSupplierOfProduct(@PathVariable int id){
		
		try {
			Supplier supplier = dao.getSupplierOfProduct(id);
			
			if(supplier == null) {
				throw new DaoException("There is no supplier ");
			}
			
			return ResponseEntity.ok(supplier);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "product id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/category/{categoryId}")
	public ResponseEntity<?> getProductsInCategory(@PathVariable int categoryId){
		
		try {
			List<Product> products = dao.getProductsInCategory(categoryId);
			return ResponseEntity.ok(products);
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "category id: " + categoryId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/supplier/{supplierId}")
	public ResponseEntity<?> getProductsOfSupplier(@PathVariable int supplierId){
		
		try {
			List<Product> products = dao.getProductsOfSupplier(supplierId);
			return ResponseEntity.ok(products);
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "supplier id: " + supplierId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	
	// ======================================= POST =======================================
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addProduct(@RequestBody Product product) {
		
		try {
			dao.addProduct(product);
			product = dao.getProduct(product.getProductId());
			return ResponseEntity.status(HttpStatus.CREATED).body(product);
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("failed to add product to db");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	
	// ======================================= PUT =======================================
	
	@RequestMapping(method = RequestMethod.PUT, path="/{id}")
	public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable Integer id) {
		
		try {
			product.setProductId(id);
			dao.updateProduct(product);
			product = dao.getProduct(product.getProductId());
			return ResponseEntity.status(HttpStatus.OK).body(product);
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("failed to update product in db");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
	

	// ======================================= DELETE =======================================
	
	@RequestMapping(method = RequestMethod.DELETE, path="/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
		
		try {
			dao.deleteProduct(id);
			Product product = dao.getProduct(id);
			return ResponseEntity.status(HttpStatus.OK).body(product);
		} catch (DaoException e) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setData(e.getMessage());
			errorMessage.setMessage("failed to delete product from db");
			return ResponseEntity.status(HttpStatus.valueOf(500)).body(errorMessage);
		}
	}
}
