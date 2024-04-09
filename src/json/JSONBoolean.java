package json;

import java.util.Iterator;

public class JSONBoolean implements JSONValue {

	private boolean bool;
	
	public JSONBoolean(boolean b) {
		bool = b;
	}
	
	@Override
	public String toString() {
		return bool ? "true" : "false";
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		if (path.length != 0)
			throw new ValueNotFoundException("trying to move to " + path[0] + " from boolean value " + bool);
		
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> clazz) {
		if (!(clazz.isInstance(bool) || clazz == boolean.class))
			throw new IllegalArgumentException(Boolean.class + " can't be converted into " + clazz);
		Boolean res = bool;
		return (T) res;
	}

	@Override
	public Iterator<JSON> iterator() {
		return new JSONBooleanIterator();
	}

	private class JSONBooleanIterator implements Iterator<JSON> {

		private boolean read;
		
		public JSONBooleanIterator() {
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
			
			return JSONBoolean.this;
		}
		
	}
	
}
