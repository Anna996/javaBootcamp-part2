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

import adjb.learn.dao.DaoException;
import adjb.learn.dao.ProductDao;
import adjb.learn.dao.SupplierDao;
import adjb.learn.models.ErrorMessage;
import adjb.learn.models.Supplier;

@RestController
@RequestMapping("/suppliers")
public class SupplierResource {
	
	@Autowired
	SupplierDao dao;
	
	@Autowired
	ProductDao productDao;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Supplier>> getAllSuppliers() throws DaoException{
		List<Supplier> suppliers = dao.getAllSupplieries();
		
		if(suppliers == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		
		return ResponseEntity.ok(suppliers);
	}
	
	@RequestMapping(method = RequestMethod.GET, path="/{id}")
	public ResponseEntity<?> getSupplierById(@PathVariable int id){
		Supplier supplier;
		
		try {
			supplier = dao.getSupplier(id);
			return ResponseEntity.ok(supplier);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "supplier id: " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addSupplier(@RequestBody Supplier supplier) {
		try {
			dao.addSupplier(supplier);
			supplier = dao.getSupplier(supplier.getSupplierId());
			return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to add this supplier to DB");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/{id}")
	public ResponseEntity<?> updateSupplier(@RequestBody Supplier supplier, @PathVariable int id) {
		try {
			supplier.setSupplierId(id);
			dao.updateSupplier(supplier);
			supplier = dao.getSupplier(id);
			return ResponseEntity.status(HttpStatus.OK).body(supplier);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to update this supplier in DB");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, path="/{id}")
	public ResponseEntity<?> deleteSupplier(@PathVariable int id) {
		try {
			dao.deleteSupplier(id);
			Supplier supplier = dao.getSupplier(id);
			productDao.deleteSupplier(id);
			return ResponseEntity.status(HttpStatus.OK).body(supplier);
			
		} catch (DaoException e) {
			ErrorMessage eMessage = ErrorMessage.getErrorMessage(e.getMessage(), "failed to delete this supplier from DB");
			return ResponseEntity.status(500).body(eMessage);
		}
	}
}
