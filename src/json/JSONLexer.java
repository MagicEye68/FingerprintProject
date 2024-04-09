package json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

class JSONLexer implements Iterator<String> {
	
	private static final int EOF = -1;
	private Reader reader;
	private int current;
	
	public JSONLexer(InputStream is) {
		if (is == null)
			throw new IllegalArgumentException("is == null");
		
		current = '\0';
		reader = new InputStreamReader(is);
		read();
	}
	
	public JSONLexer(File f) {
		if (f == null)
			throw new IllegalArgumentException("f == null");
		
		if (!(f.exists() && f.isFile()))
			throw new IllegalArgumentException("!(f.exists() && f.isFile())");
		
		current = '\0';
		try {
			reader = new FileReader(f);
		} catch (FileNotFoundException e) {
			//Blank
		}
		read();
	}
	
	public JSONLexer(String s) {
		if (s == null)
			throw new IllegalArgumentException("s == null");
		
		current = '\0';
		reader = new StringReader(s);
		read();
	}
	
	private String readToken() {		
		StringBuilder builder = new StringBuilder();
		while (!(isWhitespace() || isSpecial())) {
			if ((char) current == '\\')
				read();
			builder.append((char) current);
			read();
		}
		
		skipWhitespaces();
		return builder.toString();
	}
	
	private String readSpecial() {
		StringBuilder builder = new StringBuilder();
		builder.append((char) current);
		current = read();
		skipWhitespaces();
		return builder.toString();
	}
	
	private void skipWhitespaces() {
		while (isWhitespace())
			read();
	}
	
	private boolean isWhitespace() {
		return current == ' ' || current == '\t' || current == '\n' || current == '\r';
	}

	private boolean isSpecial() {
		return current == '{' || current == '}' || current == '\"' || current == ',' || current == ':' || current == '[' || current == ']';
	}
	
	private boolean isNumeric() {
		return current >= '0' && current <= '9';
	}
	
	private boolean isAlphabetical() {
		return (current >= 'a' && current <= 'z') || (current >= 'A' && current <= 'Z') || (current == '-') || (current == '/');
	}
	
	private boolean isEscapedCharacter() {
		return current == '\\';
	}
	
	private boolean isAlphaNumeric() {
		return isAlphabetical() || isNumeric();
	}
	
	private boolean isStringToken() {
		return current == '&' || current == '_' || current == '<' || current == '>' || current == '(' || current == ')';
	}
	
	private int read() {
		try {
			current = reader.read();
		} catch (Throwable throwable) {
			current = EOF;
		}

		return current;
	}
	
	@Override
	public boolean hasNext() {
		return current != EOF;
	}

	@Override
	public String next() {
		if (current == EOF)
			throw new IllegalStateException("current == EOF");
		
		if (isAlphaNumeric() || isStringToken())
			return readToken();

		if (isSpecial())
			return readSpecial();
		
		if (isEscapedCharacter())
			return readToken();
		
		throw new IllegalStateException("not expected token " + (char)current);
	}
	
}
