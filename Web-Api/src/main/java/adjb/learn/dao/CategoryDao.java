package adjb.learn.dao;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import adjb.learn.models.Category;

@Transactional(readOnly = true, rollbackFor = { DaoException.class })
public interface CategoryDao {
	
	@Transactional(readOnly = false)
	public void addCategory(Category category) throws DaoException;

	public Category getCategory(int id) throws DaoException;

	@Transactional(readOnly = false)
	public void updateCategory(Category category) throws DaoException;

	@Transactional(readOnly = false)
	public void deleteCategory(int id) throws DaoException;

	public List<Category> getAllCategories() throws DaoException;
}
