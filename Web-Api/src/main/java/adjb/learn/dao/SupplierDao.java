package adjb.learn.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import adjb.learn.models.Supplier;

@Transactional(readOnly = true, rollbackFor = {DaoException.class})
public interface SupplierDao {
	
	@Transactional(readOnly = false)
	public void addSupplier(Supplier supplier) throws DaoException;
	
	public Supplier getSupplier(int id) throws DaoException;
	
	@Transactional(readOnly = false)
	public void updateSupplier(Supplier supplier) throws DaoException;
	
	@Transactional(readOnly = false)
	public void deleteSupplier(int id) throws DaoException;
	
	public List<Supplier> getAllSupplieries() throws DaoException;
}
