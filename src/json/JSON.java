package json;

public interface JSON extends Iterable<JSON> {
	
	public String toString();
		
	public JSON traverse(String[] path) throws ValueNotFoundException ;
	
	public <T> T getObject(Class<T> clazz);

}
