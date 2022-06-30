package adjb.learn.dao;

import java.util.List;

import org.hibernate.cfg.CreateKeySecondPass;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import adjb.learn.models.Category;
import adjb.learn.models.Product;
import adjb.learn.models.Supplier;

@SuppressWarnings("unchecked")
@Component(value = "htDao")
public class HibernateTemplateProductDao implements ProductDao {

	@Autowired
	private HibernateTemplate template;

	@Override
	public void addProduct(Product product) throws DaoException {
		// open session / connection to db
		template.persist(product);
		// close session
	}

	@Override
	public void updateProduct(Product product) throws DaoException {
		template.merge(product);
	}

	@Override
	public Product getProduct(Integer productId) throws DaoException {
		Product product = template.get(Product.class, productId);
		
		if(product == null) {
			throw new DaoException("Product is null");
		}
		
		return product;
	}

	@Override
	public void deleteProduct(Integer productId) throws DaoException {
		Product product = getProduct(productId);
		product.setDiscontinued(1);
		updateProduct(product);
	}

	@Override
	public List<Product> getAllProducts() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		return (List<Product>) template.findByCriteria(criteria);
	}

	@Override
	public List<Product> getProductsByPriceRange(Double min, Double max) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		Criterion criterion = Restrictions.between("unitPrice", min, max);
		criteria.add(criterion);
		return (List<Product>) template.findByCriteria(criteria);
	}

	@Override
	public List<Product> getProductsInCategory(Integer categoryId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.eq("categoryId", categoryId));
		List<Product> products = (List<Product>) template.findByCriteria(criteria);
		
		if(products == null) {
			throw new DaoException("There are no products in this category");
		}
		
		return products;
	}
	

	@Override
	public List<Product> getProductsOfSupplier(Integer supplierId) throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.eq("supplierId", supplierId));
		
		List<Product> products = (List<Product>)template.findByCriteria(criteria);
		
		if(products == null) {
			throw new DaoException("There are no products from this supplier");
		}
		
		return products;
	}

	@Override
	public List<Product> getProductsNotInStock() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.eq("unitsInStock", 0));
		return (List<Product>) template.findByCriteria(criteria);
	}

	@Override
	public List<Product> getProductsOnOrder() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.gt("unitsOnOrder", 0));
		return (List<Product>) template.findByCriteria(criteria);
	}

	@Override
	public List<Product> getDiscontinuedProducts() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.gt("discontinued", 1));
		return (List<Product>) template.findByCriteria(criteria);
	}

	@Override
	public long count() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.setProjection(Projections.rowCount());
		return (Long) template.findByCriteria(criteria).get(0);
	}
	
	@Override
	public void deleteCategory(Integer categoryId) throws DaoException {
		List<Product> products = getProductsInCategory(categoryId);
		
		for (Product product : products) {
			product.setCategory(null);
			updateProduct(product);
		}
	}

	@Override
	public void deleteSupplier(Integer supplierId) throws DaoException {
		List<Product> products = getProductsOfSupplier(supplierId);
		
		for (Product product : products) {
			product.setSupplier(null);
			updateProduct(product);
		}
	}

	@Override
	public Category getCategoryOfProduct(Integer productId) throws DaoException {
		Product product = getProduct(productId);
		return product.getCategory();
	}

	@Override
	public Supplier getSupplierOfProduct(Integer productId) throws DaoException {
		Product product = getProduct(productId);
		return product.getSupplier();
	}
}
