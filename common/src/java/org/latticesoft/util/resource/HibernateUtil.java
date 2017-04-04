package org.latticesoft.util.resource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateUtil {

	/** Logger for this class */
	private static final Log log = LogFactory.getLog(HibernateUtil.class);

	public static List search(Class dbClass, Criterion criterion, Object orderClause) throws Exception {
		List result = null;
		Session session = null;
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
if (log.isInfoEnabled()) { log.info("Searching record from database : " + dbClass); }
			Criteria criteria = session.createCriteria((Class) dbClass);
			if (criterion != null) {
				criteria.add(criterion);
			}
			if (orderClause != null) {
				if (orderClause instanceof String) {
					addOrder((Class) dbClass, criteria, (String) orderClause);
				}
				if (orderClause instanceof Collection) {
					Iterator iter = ((Collection) orderClause).iterator();
					while (iter.hasNext()) {
						addOrder((Class) dbClass, criteria, (String) iter.next());
					}
				}
			}
			result = criteria.list();
			HibernateSessionFactory.commitTransaction();
if (log.isInfoEnabled()) { log.info("Search completed successfully"); }
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return result;
	}

	public static Object load(Class dbClass, Serializable primaryKey) throws Exception {
		Object result = null;
		Session session = null;
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
if (log.isInfoEnabled()) { log.info("Searching record from database : " + dbClass); }
			result = session.load((Class) dbClass, primaryKey);
			HibernateSessionFactory.commitTransaction();
if (log.isInfoEnabled()) { log.info("Search completed successfully"); }
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return result;
	}

	public static boolean add(Object dbClass) throws Exception {
		boolean result = false;
		Session session = null;
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
if (log.isInfoEnabled()) { log.info("Inserting record to database : " + dbClass); }
			Serializable s = session.save(dbClass);
			HibernateSessionFactory.commitTransaction();
if (log.isInfoEnabled()) { log.info("Insert successful"); }
			if (s instanceof Boolean) {
				result = ((Boolean)s).booleanValue();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return result;
	}

	public static boolean update(Object dbClass) throws Exception {
		boolean result = false;
		Session session = null;
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
if (log.isInfoEnabled()) { log.info("Updating record : " + dbClass); }
			session.update(dbClass);
			HibernateSessionFactory.commitTransaction();
			result = true;
if (log.isInfoEnabled()) { log.info("Update successful"); }
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return result;
	}

	public static boolean delete(Object dbClass) throws Exception {
		boolean result = false;
		Session session = null;
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
if (log.isInfoEnabled()) { log.info("Deleting record from database : " + dbClass); }
			session.delete(dbClass);
			HibernateSessionFactory.commitTransaction();
if (log.isInfoEnabled()) { log.info("Delete successful"); }
			result = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return result;
	}

	protected static void addOrder(Class dbClass, Criteria criteria, String order) {
		if (order == null)
			return;
		order = order.trim();
		int index = order.indexOf(" ");
		boolean isDesc = false;
		if (index > -1) {
			String ascDesc = order.substring(index, order.length());
			if (ascDesc != null) {
				ascDesc = ascDesc.trim();
			}
			if ("desc".equalsIgnoreCase(ascDesc)) {
				isDesc = true;
			} else {
				isDesc = false;
			}
			order = order.substring(0, index);
		}
		if (order.indexOf(".") <= 0) {
			if (isDesc) {
				criteria.addOrder(Order.desc(order));
			} else {
				criteria.addOrder(Order.asc(order));
			}
		} else {
			Criteria subCrit = criteria.createCriteria(order.substring(0, order.indexOf(".")));
			if (isDesc) {
				subCrit.addOrder(Order.desc(order.substring(order.indexOf(".") + 1, order.length())));
			} else {
				subCrit.addOrder(Order.asc(order.substring(order.indexOf(".") + 1, order.length())));
			}
		}
	}
	
	public static List searchByHQL(String hql, Map param) throws Exception {
		if (hql == null) return null;
		Session session = null;
		List retVal = null;
if (log.isInfoEnabled()) { 
	log.info("HQL: " + hql);
	log.info(param);
}
		try {
			session = HibernateSessionFactory.getCurrentSession(); 
			HibernateSessionFactory.beginTransaction();
			Query query = session.createQuery(hql);
			if (param != null) {
				Iterator iter = param.keySet().iterator();
				while (iter.hasNext()) {
					Object key = iter.next();
					Object value = param.get(key);
if (log.isInfoEnabled()) {
	log.info("Key  : " + key);
	log.info("Value: " + value);
}
					if (key != null && value != null) {
if (log.isInfoEnabled()) {
	log.info("ValueClass: " + value.getClass());
}
						if (value instanceof String) {
							query.setString(key.toString(), value.toString());
						} else if (value instanceof BigDecimal) {
							query.setBigDecimal(key.toString(), (BigDecimal)value);
						} else if (value instanceof BigInteger) {
							query.setBigInteger(key.toString(), (BigInteger)value);
						} else if (value instanceof byte[]) {
							query.setBinary(key.toString(), (byte[])value);
						} else if (value instanceof Boolean) {
							query.setBoolean(key.toString(), ((Boolean)value).booleanValue());
						} else if (value instanceof Byte) {
							query.setByte(key.toString(), ((Byte)value).byteValue());
						} else if (value instanceof Calendar) {
							query.setCalendar(key.toString(), ((Calendar)value));
						} else if (value instanceof Character) {
							query.setCharacter(key.toString(), ((Character)value).charValue());
						} else if (value instanceof Date) {
							query.setDate(key.toString(), ((Date)value));
						} else if (value instanceof Double) {
							query.setDouble(key.toString(), ((Double)value).doubleValue());
						} else if (value instanceof Object) {
							query.setDate(key.toString(), ((Date)value));
						} else if (value instanceof Float) {
							query.setFloat(key.toString(), ((Float)value).floatValue());
						} else if (value instanceof Integer) {
							query.setInteger(key.toString(), ((Integer)value).intValue());
						} else if (value instanceof Long) {
							query.setLong(key.toString(), ((Long)value).longValue());
						} else if (value instanceof Short) {
							query.setShort(key.toString(), ((Short)value).shortValue());
						} else if (value instanceof Serializable) {
							query.setSerializable(key.toString(), ((Serializable)value));
						} else if (value instanceof java.sql.Time) {
							query.setTime(key.toString(), ((java.sql.Time)value));
						} else if (value instanceof java.sql.Timestamp) {
							query.setTimestamp(key.toString(), ((java.sql.Timestamp)value));
						} else if (value instanceof Object) {
							query.setParameter(key.toString(), value);
						}
					}
				}
			}
			retVal = query.list();
			HibernateSessionFactory.commitTransaction();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Database Operation Failed", e);
			}
			HibernateSessionFactory.rollbackTransaction();
			throw e;
		} finally {
			HibernateSessionFactory.closeSession();
		}
		return retVal;
	}

}
