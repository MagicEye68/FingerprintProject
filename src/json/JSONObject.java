package json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONObject implements JSONContainer {

	private List<JSONMember> elems;
	
	public JSONObject() {
		elems = new ArrayList<JSONMember>();
	}
	
	public void add(JSONMember elem) {
		if (elem == null)
			throw new IllegalArgumentException("elem == null");
		
		elems.add(elem);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		Iterator<JSONMember> iter = elems.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next().toString());
			if (iter.hasNext())
				builder.append(",");
		}
		builder.append("}");
		
		return builder.toString();
	}
	
	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		if (path.length == 0)
			return this;
		
		String next = path[0];
		JSONMember member = getMemberForKey(next);
		String[] newPath = JSONContainer.consumePath(path);
		return member.traverse(newPath);
	}

	@Override
	public <T> T getObject(Class<T> clazz) {
		try {
			T res = clazz.getDeclaredConstructor().newInstance();
			Field[] fields = clazz.getFields();
			for (Field f : fields) {
				Class<?> fieldType = f.getType();
				String fname = f.getName();
				try {
					JSONMember member = getMemberForKey(fname);
					f.set(res, member.getObject(fieldType));
				} catch (MemberNotFoundException e) {
					continue;
				}
			}
			return res;
		} catch (Throwable e) {
			throw new IllegalValueException(e.getMessage());
		}

	}
	
	private JSONMember getMemberForKey(String key) {
		for (int i = 0; i != elems.size(); ++i) {
			JSONMember member = elems.get(i);
			if (member.getKey().equals(key)) 
				return member;
			
		}
		
		throw new MemberNotFoundException("member not found for key " + key);
	}

	//TODO to implements
	@Override
	public Iterator<JSON> iterator() {
		return new JSONObjectIterator();
	}
	
	private class JSONObjectIterator implements Iterator<JSON> {
		
		private Iterator<JSONMember> iter;
		
		public JSONObjectIterator() {
			iter = elems.iterator();
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public JSON next() {
			return iter.next();
		}
		
	}
	
}
