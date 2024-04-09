package json;

import java.lang.reflect.Field;

class JSONTransformer {
	
	JSONTransformer() {}
	
	JSON transform(Object o) throws IllegalAccessException {
		if (o == null)
			return new JSONNull();
		
		Class<?> clazz = o.getClass();
		if (clazz.getName().charAt(0) == '[') { //Array
			return transformJSONArray((Object[]) o);
		} else if (isPrimitive(clazz)) { //Primitive value
			return transformJSONPrimitive(o, clazz);
		} else {	//Object
			return tranformJSONObject(o, clazz);
		}
	}
	
	private boolean isPrimitive(Class<?> clazz) {
		if (clazz.equals(String.class))
			return true;
		
		if (clazz.equals(Integer.class) || clazz == Integer.TYPE)
			return true;
		
		if (clazz.equals(Double.class) || clazz == Double.TYPE)
			return true;
		
		if (clazz.equals(Boolean.class) || clazz == Boolean.TYPE)
			return true;
		
		if (clazz.equals(Long.class) || clazz == Long.TYPE)
			return true;
		
		return false;
	}
	
	private JSON transformJSONArray(Object[] array) throws IllegalAccessException {
		JSONArray res = new JSONArray();
		for (Object o : array)
			res.add(this.transform(o));
		
		return res;
	}
	
	private JSON transformJSONPrimitive(Object obj, Class<?> clazz) {
		if (clazz.equals(String.class))
			return new JSONString((String) obj);
		
		if (clazz.equals(Integer.class) || clazz == Integer.TYPE)
			return new JSONNumber((int) obj);
		
		if (clazz.equals(Double.class) || clazz == Double.TYPE)
			return new JSONNumber((double) obj);
		
		if (clazz.equals(Boolean.class) || clazz == Boolean.TYPE)
			return new JSONBoolean((boolean) obj);
		
		//might get narrowed to int, to implements long type in JSONNumber
		if (clazz.equals(Long.class) || clazz == Long.TYPE)
			return new JSONNumber((long) obj);
		
		throw new IllegalStateException("unreacheable");
	}
	
	private JSON tranformJSONObject(Object obj, Class<?> clazz) throws IllegalAccessException {
		JSONObject res = new JSONObject();
		for (Field f : clazz.getFields()) {
			String key = f.getName();
			JSON value = this.transform(f.get(obj));
			res.add(new JSONMember(key, value));
		}
		
		return res;
	}
	
}
