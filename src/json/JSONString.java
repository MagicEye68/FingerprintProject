package json;

import java.util.Iterator;

public class JSONString implements JSONValue {

	private String str;
	
	public JSONString(String s) {
		if (s == null)
			throw new IllegalArgumentException("s == null");
		
		str = s;
	}
	
	@Override
	public String toString() {
		//return "\"" + str + "\"";
		return str;
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		if (path.length != 0)
			throw new ValueNotFoundException("trying to move to " + path[0] + " from string " + str);
		
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> clazz) {
		if (!clazz.isInstance(str))
			throw new IllegalArgumentException(String.class + " can't be converted into " + clazz);
		return (T) str;
	}

	@Override
	public Iterator<JSON> iterator() {
		return new JSONStringIterator();
	}
	
	private class JSONStringIterator implements Iterator<JSON> {

		private boolean read;
		
		public JSONStringIterator() {
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
			
			return JSONString.this;
		}
		
	}

}
