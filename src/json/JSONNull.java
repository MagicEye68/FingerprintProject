package json;

import java.util.Iterator;

public class JSONNull implements JSONValue {

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		if (path.length != 0)
			throw new ValueNotFoundException("trying to move to " + path[0] + " from null");
		
		return this;
	}

	@Override
	public <T> T getObject(Class<T> clazz) {
		return null;
	}

	@Override
	public Iterator<JSON> iterator() {
		return new JSONNullIterator();
	}
	
	private class JSONNullIterator implements Iterator<JSON> {

		private boolean read;
		
		public JSONNullIterator() {
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
			
			return JSONNull.this;
		}
		
	}

}
