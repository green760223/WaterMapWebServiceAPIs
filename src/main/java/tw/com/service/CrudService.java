package tw.com.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.com.util.DbUtil;
import tw.com.util.FrensworkzException;


public class CrudService<T, ID extends Serializable> {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> persistentClass;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public CrudService()
    {
        Type type = getClass().getGenericSuperclass();
        do {
            if (type != null && type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                if (paramType.getRawType().equals(CrudService.class))
                {
                    persistentClass = (Class<T>) paramType.getActualTypeArguments()[0];
                    type = null;
                }
            }
            if (type instanceof Class) {
                type = ( (Class) type ).getGenericSuperclass();
            }
            else {
                type = null;
            }
        } while (type != null);
    }

    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }
    
    public T getById(ID id) throws FrensworkzException
    {
        return getById(id, false);
    }

    @SuppressWarnings("unchecked")
    public T getById(ID id, boolean lock) throws FrensworkzException
    {
        T entity;
        Session session = DbUtil.getCurrentSession();
    	session.beginTransaction();
        entity = (T)session.get(getPersistentClass(), id);
        if (entity == null) {
        	throw new FrensworkzException("資料不存在 ");
        }
//        if (lock)
//        	entity = (T)session.load(getPersistentClass(), id, LockMode.UPGRADE);
//        else
//            entity = (T)session.load(getPersistentClass(), id);
//
        return entity;
    }

    public List<T> getAll()
    {
    	return getByCriteria();
    }
     /**
     * 儲存
     * @param entity
     * @return
     */
    public T save(final T entity)
    {
    	Session session = DbUtil.getCurrentSession();
    	
    	try {
    	
	    	if (!session.getTransaction().isActive()) {
	    		session.beginTransaction();
	    	}
	    	session.saveOrUpdate(entity);
	    	session.getTransaction().commit();
    	
    	} catch (Exception e) {
    		session.getTransaction().rollback();
    		try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	
        return entity;
    }
    /**
     * 刪除
     * @param entity
     * @throws FrensworkzException 
     */
    public void delete(final T entity) throws FrensworkzException
    {
    	Session session = DbUtil.getCurrentSession();
    	if (!session.getTransaction().isActive()) {
    		session.beginTransaction();
    	}
    	try {
    		session.delete(entity);
    		session.getTransaction().commit();
    	} catch (ConstraintViolationException e) {
    		e.printStackTrace();
    		session.getTransaction().rollback();
    		throw new FrensworkzException("違反完整性限制，無法刪除，可能有資料關聯到此資料");
    	} catch (Exception e) {
    		e.printStackTrace();
    		session.getTransaction().rollback();
    		throw new FrensworkzException("刪除失敗");
    	}
  
    }
    /**
     * 依id刪除
     * @param id
     * @throws FrensworkzException 
     */
    public void deleteById(ID id) throws FrensworkzException
    {
        delete(getById(id, false));
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByCriteria(final Criterion... criterions)
    {
    	Session session = DbUtil.getCurrentSession();
    	session.beginTransaction();
    	Criteria crit = session.createCriteria(getPersistentClass()); 
    	for (Criterion c : criterions)   
        {   
             crit.add(c);   
    	} 
    	return crit.list();   
    }

    @SuppressWarnings("unchecked")
    protected List<T> getByQuery(String sql, Object... parameters)
    {
    	if (parameters == null)
        {
        	return (List<T>)DbUtil.getCurrentSession().createQuery(sql).list();
        }
        else
        {
        	Query query=DbUtil.getCurrentSession().createQuery(sql);
        	for(int i=0,len=parameters.length;i<len;i++){
        		query.setParameter(i,parameters[i]);
        	}
        	return (List<T>)query.list();
        }
    }

}