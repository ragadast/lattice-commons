/*
 * Copyright Jurong Port Pte Ltd
 * Created on Sep 19, 2014
 */
package org.latticesoft.util.common;

import java.math.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class InitUtil {
	private static final Log log = LogFactory.getLog(InitUtil.class);

	private boolean isSystemPriority = true;
	private String propertyKeyPrefix = null;
	private boolean isToResolve = false;
	
	/** @return Returns the isSystemPriority. */
	public boolean isSystemPriority() {
		return (this.isSystemPriority);
	}

	/** @param isSystemPriority The isSystemPriority to set. */
	public void setSystemPriority(boolean isSystemPriority) {
		this.isSystemPriority = isSystemPriority;
	}

	/** @return Returns the propertyKeyPrefix. */
	public String getPropertyKeyPrefix() {
		return (this.propertyKeyPrefix);
	}

	/** @param propertyKeyPrefix The propertyKeyPrefix to set. */
	public void setPropertyKeyPrefix(String propertyKeyPrefix) {
		this.propertyKeyPrefix = propertyKeyPrefix;
	}

	/** @return Returns the isToResolve. */
	public boolean isToResolve() {
		return (this.isToResolve);
	}

	/** @param isToResolve The isToResolve to set. */
	public void setToResolve(boolean isToResolve) {
		this.isToResolve = isToResolve;
	}

	public String getStringProperty(String key) {
		if (key == null || key.equalsIgnoreCase("")) {
			return null;
		}
		PropertyMap pm = PropertyMap.getInstance();
		String systemValue = System.getProperty(key);;
		String propKey = (propertyKeyPrefix != null) ? propertyKeyPrefix + "_" + key : key;
		String propValue = null;
		if (this.isToResolve) {
			propValue = pm.getString("${" + propKey + "}");
		} else {
			propValue = pm.getString(propKey);
		}
		
		String retVal = null;
		if (this.isSystemPriority) {
			retVal = systemValue;
			if (retVal == null) {
				retVal = propValue;
			}
		} else {
			retVal = propValue;
			if (retVal == null) {
				retVal = systemValue;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(key + " -> [" + retVal + "]");
		}
		return retVal;
	}

	public int getIntProperty(String key) {
		String s = this.getStringProperty(key);
		int retVal = 0;
		if (s != null) {
			retVal = NumeralUtil.parseInt(s);
		}
		return retVal;
	}
	
	public long getLongProperty(String key) {
		String s = this.getStringProperty(key);
		long retVal = 0;
		if (s != null) {
			retVal = NumeralUtil.parseLong(s);
		}
		return retVal;
	}
	
	public double getDoubleProperty(String key) {
		String s = this.getStringProperty(key);
		double retVal = 0.0;
		if (s != null) {
			retVal = NumeralUtil.parseDouble(s);
		}
		return retVal;
	}
	
	public byte getByteProperty(String key) {
		String s = this.getStringProperty(key);
		byte retVal = 0;
		if (s != null) {
			retVal = NumeralUtil.parseByte(s);
		}
		return retVal;
	}
	
	public BigDecimal getBigDecimalProperty(String key) {
		String s = this.getStringProperty(key);
		BigDecimal retVal = new BigDecimal("0");
		if (s != null) {
			retVal = NumeralUtil.parseBigDecimal(s);
		}
		return retVal;
	}
	
	public float getFloatProperty(String key) {
		String s = this.getStringProperty(key);
		float retVal = 0;
		if (s != null) {
			retVal = NumeralUtil.parseFloat(s);
		}
		return retVal;
	}
	
	public short getShortProperty(String key) {
		String s = this.getStringProperty(key);
		short retVal = 0;
		if (s != null) {
			retVal = NumeralUtil.parseShort(s);
		}
		return retVal;
	}
	
	public boolean getBooleanProperty(String key) {
		String s = this.getStringProperty(key);
		boolean retVal = false;
		if (s != null) {
			retVal = "true".equalsIgnoreCase(s) || 
					"yes".equalsIgnoreCase(s) || "y".equalsIgnoreCase(s) ||
					"1".equalsIgnoreCase(s) ||
					"on".equalsIgnoreCase(s);
		}
		return retVal;
	}

}
