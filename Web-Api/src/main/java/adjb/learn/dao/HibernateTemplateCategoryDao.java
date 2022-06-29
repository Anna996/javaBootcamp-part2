package adjb.learn.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import adjb.learn.models.Category;

@SuppressWarnings("unchecked")
@Component
public class HibernateTemplateCategoryDao implements CategoryDao {
	
	@Autowired
	HibernateTemplate template;

	@Override
	public void addCategory(Category category) throws DaoException {
		template.persist(category);
	}
	
	@Override
	public Category getCategory(int id) throws DaoException {
		Category category = template.get(Category.class, id);
		
		if(category == null) {
			throw new DaoException("No such category in DB");
		}
		
		return category;
	}

	@Override
	public void updateCategory(Category category) throws DaoException {
		template.merge(category);
	}

	@Override
	public void deleteCategory(int id) throws DaoException {
		Category category = getCategory(id);
		category.setCatName("-1");
		updateCategory(category);
	}

	@Override
	public List<Category> getAllCategories() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Category.class);
		return (List<Category>)template.findByCriteria(criteria);
	}
}
