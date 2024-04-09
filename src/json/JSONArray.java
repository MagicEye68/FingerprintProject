package json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONArray implements JSONContainer {

	private List<JSON> elems;
	
	public JSONArray() {
		elems = new ArrayList<JSON>();
	}
	
	public void add(JSON j) {
		if (j == null)
			throw new IllegalArgumentException("j == null");
		
		elems.add(j);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		Iterator<JSON> iter = elems.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next().toString());
			if (iter.hasNext())
				builder.append(", ");
		}
		builder.append(']');
		return builder.toString();
	}

	@Override
	public JSON traverse(String[] path) throws ValueNotFoundException {
		if (path == null)
			throw new IllegalArgumentException("path == null");
		
		if (path.length == 0)
			return this;
		
		String next = path[0];
		int index = Integer.parseInt(next);
		if (index < 0 || index >= elems.size())
			throw new ValueNotFoundException("can't reach index " + index + ", array size " + elems.size());
		
		JSON elem = elems.get(index);
		String[] newPath = JSONContainer.consumePath(path);
		return elem.traverse(newPath);
	}

	@Override
	public <T> T getObject(Class<T> clazz) {
		if (clazz == null)
			throw new IllegalArgumentException("clazz == null");

		String className = clazz.getName();
		Class<?> componentType = clazz.componentType();
		if (!(className.charAt(0) == '['))
			throw new IllegalArgumentException("clazz is not an array class descriptor");
		
		@SuppressWarnings("unchecked")
		T res = (T) Array.newInstance(clazz.getComponentType(), elems.size());
		for (int i = 0; i != elems.size(); ++i) {
			Object value = elems.get(i).getObject(componentType);
			Array.set(res, i, value);
		}
		
		return res;
	}

	@Override
	public Iterator<JSON> iterator() {
		return elems.iterator();
	}


	
	
	
}
