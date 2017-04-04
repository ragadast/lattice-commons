package org.latticesoft.util.common;

public class TestBean implements java.io.Serializable {
    public static final long serialVersionUID = 12435L;
	private String name;
	private String message;
	private String format;

	public void setMessage(String message) {
		this.message = message;
	}
	public void setName(String name) { 
		this.name = name;
	}
	public String getMessage() {
		return this.message;
	}
	public String getName() {
		return this.name;
	}
	public String getFormat() {
		return (this.format);
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String sayHello() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hello there ");
		sb.append(name);
		//System.out.println(sb.toString());
		return sb.toString();
	}

	public String sayHello2(String extra) {
		StringBuffer sb = new StringBuffer();
		sb.append("Hello there :\"");
		sb.append(name);
		sb.append(".\"");
		sb.append(" ");
		sb.append(extra);
		//System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	* Converts the class in a string form
	* @returns the class in a string form.
	*/
	public String toString() {
		String s = StringUtil.formatObjectToString(this);
		if (s != null) return s;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(this.getClass().getName());
		sb.append("]");
		return sb.toString();
	}
}