package json;

import java.util.Iterator;

public class JSONNumber implements JSONValue {

	private int integer;
	private double decimal;
	private Kind kind;
	
	private boolean isNumeric(char c) {
		return c >= '0' && c <= '9';
	}
	
	public JSONNumber(String value) {
		int i = 0;
		int dots = 0;
		kind = Kind.INT;
		if (value.charAt(0) == '-')
			++i;
		
		for (; i != value.length(); ++i) {
			if (value.charAt(i) == '.' && i != 0) {
				++dots;
				kind = Kind.DOUBLE;
			} else if (!isNumeric(value.charAt(i))) {
				throw new IllegalArgumentException(value + " is not a number");
			}
		}
		if (dots > 1)
			throw new IllegalArgumentException(value + " is not a number");
		
		if (kind == Kind.DOUBLE) {
			decimal = Double.parseDouble(value);
			integer = 0;
		} else {
			integer = Integer.parseInt(value);
			decimal = 0;
		}
	}
	
	public JSONNumber(int value) {
		kind = Kind.INT;
		integer = value;
		decimal = 0;
	}
	
	public JSONNumber(double value) {
		kind = Kind.DOUBLE;
		decimal = value;
		integer = 0;
	}
	
	@Override
	public String toString() {
		if (kind == Kind.DOUBLE) {
			return Double.toString(decimal);
		} else {
			return Integer.toString(integer);
		}
	}
	
	private enum Kind {
		INT,
		DOUBLE
	}

	private Object getValue() {
		if (kind == Kind.INT)
			return integer;
		else 
			return decimal;
				
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		Object res = getValue();
		if (path.length != 0)
			throw new ValueNotFoundException("trying to move to " + path[0] + " from numeric value " + res);
		
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> clazz) {
		if (kind == Kind.INT) {
			if (!(clazz.isInstance(integer) || clazz == int.class))
				throw new IllegalArgumentException(Integer.class + " can't be converted into " + clazz);
			Integer i = integer;
			return (T) i;
		} else {
			if (!clazz.isInstance(decimal) || clazz == double.class)
				throw new IllegalArgumentException(Double.class + " can't be converted into " + clazz);
			Double d = decimal;
			return (T) d;
		}

	}

	@Override
	public Iterator<JSON> iterator() {
		return new JSONNumberIterator();
	}
	
	private class JSONNumberIterator implements Iterator<JSON> {
		
		private boolean read;
		
		public JSONNumberIterator() {
			read = false;
		}
		
		@Override
		public boolean hasNext() {
			return !read;
		}

		@Override
		public JSON next() {
			if (read)
				throw new IllegalStateException("read");
			
			return JSONNumber.this;
		}
		
	}

}
