package json;

public interface JSONContainer extends JSON {
	
	static String[] consumePath(String[] path) {
		String[] newPath = new String[path.length - 1];
		for (int i = 1; i != path.length; ++i)
			newPath[i - 1] = path[i];
		
		return newPath;
	}
	
}
