package adjb.learn.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import adjb.learn.models.Supplier;

@SuppressWarnings("unchecked")
@Component
public class HibernateTemplateSupplierDao implements SupplierDao {

	@Autowired
	HibernateTemplate template;

	@Override
	public void addSupplier(Supplier supplier) throws DaoException {
		template.persist(supplier);
	}

	@Override
	public Supplier getSupplier(int id) throws DaoException {
		Supplier supplier = template.get(Supplier.class, id);

		if (supplier == null) {
			throw new DaoException("There is no such supplier in DB");
		}

		return supplier;
	}

	@Override
	public void updateSupplier(Supplier supplier) throws DaoException {
		template.merge(supplier);
	}

	@Override
	public void deleteSupplier(int id) throws DaoException {
		Supplier supplier = getSupplier(id);
		supplier.setInActive("1");
		updateSupplier(supplier);
	}

	@Override
	public List<Supplier> getAllSupplieries() throws DaoException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Supplier.class);
		return (List<Supplier>) template.findByCriteria(criteria);
	}
}
