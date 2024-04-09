package json;

import java.util.Iterator;

public class JSONMember implements JSON {

	private String key;
	private JSON value;
	
	public JSONMember(String key, JSON value) {
		if (key == null)
			throw new IllegalArgumentException("key == null");
		if (value == null)
			throw new IllegalArgumentException("value == null");
		
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "\"" + key + "\":" + value.toString();
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		return value.traverse(path);
	}
	
	public String getKey() {
		return key;
	}

	@Override
	public <T> T getObject(Class<T> clazz) {
		return value.getObject(clazz);
	}

	@Override
	public Iterator<JSON> iterator() {
		return new JSONMemberIterator();
	}
	
	private class JSONMemberIterator implements Iterator<JSON> {
		
		private boolean read;
		
		public JSONMemberIterator() {
			read = false;
		}
		
		@Override
		public boolean hasNext() {
			return !read;
		}

		//WARNING: maybe we want to iterate on JSONMember.this and not on JSONMember.this.value
		@Override
		public JSON next() {
			if (read)
				throw new IllegalStateException("read");
			
			return JSONMember.this.value;
		}
	}

	
	
}
