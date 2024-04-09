package json;

class JSONSyntacticAnalyzer {

	private JSONLexer lexer;
	private String lexeme;
	private boolean read;
	
	public JSONSyntacticAnalyzer(JSONLexer lexer) {
		if (lexer == null)
			throw new IllegalArgumentException("lexer == null");
		
		this.lexer = lexer;
		lexeme = null;
		read = true;
	}
	
	private boolean isNumeric(String lexeme) {
		if (lexeme.charAt(0) == '-')
			return true;
		
		for (int i = 0; i != lexeme.length(); ++i) {
			if (!((lexeme.charAt(i) >= '0' && lexeme.charAt(i) <= '9') || (lexeme.charAt(i) == '.')))
				return false;
				
		}
		return true;
	}
	
	public JSON parse() {
		if (read)
			nextLexeme();
		if (lexeme.equals("{")) {	//object
			read = true;
			return parseJSONObject();
		} else if (lexeme.equals("[")) { //array
			read = true;
			return parseJSONArray();
		} else if (lexeme.equals("\"")) {	//string or member
			String key = nextString();
			if (!(lexeme.equals("\"")))
				throw new InvalidJSONFormat("expected \", found " + lexeme);
			nextLexeme();
			if (lexeme.equals(":")) {	//member
				read = true;
				JSON value = parse();
				return new JSONMember(key, value);
			} else {	//string
				return new JSONString(key);
			}
		} else if (isNumeric(lexeme)) { //number
			read = true;
			return new JSONNumber(lexeme);
		} else if (lexeme.equals("null")) {	//null
			read = true;
			return new JSONNull();
		} else if (lexeme.equals("true")) {	//boolean
			read = true;
			return new JSONBoolean(true);
		} else if (lexeme.equals("false")) {
			read = true;
			return new JSONBoolean(false);
		}
		
		throw new InvalidJSONFormat("invalid format string for lexeme " + lexeme);
	}
	
	private JSONObject parseJSONObject() {
		if (read)
			nextLexeme();
		JSONObject o = new JSONObject();
		if (lexeme.equals("}")) {
			read = true;
			return o;
		}
		JSONMember member = parseJSONMember();
		o.add(member);
		if (read)
			nextLexeme();
		while (lexeme.equals(",")) {
			read = true;
			member = parseJSONMember();
			if (read)
				nextLexeme();
			o.add(member);
		}
		
		read = true;
		if (!(lexeme.equals("}")))
			throw new InvalidJSONFormat("expected }, found " + lexeme);
		return o;
	}
	
	private JSONMember parseJSONMember() {
		if (read)
			nextLexeme();
		if (!(lexeme.equals("\"")))
			throw new InvalidJSONFormat("expected \", found " + lexeme);
		String key = nextString();
		if (!(lexeme.equals("\"")))
			throw new InvalidJSONFormat("expected \", found " + lexeme);
		nextLexeme();
		if (!(lexeme.equals(":")))
			throw new InvalidJSONFormat("expected :, found " + lexeme);
		
		read = true;
		JSON value = parse();
		
		return new JSONMember(key, value);
	}
	
	private JSONArray parseJSONArray() {
		if (read)
			nextLexeme();
		JSONArray array = new JSONArray();
		if (lexeme.equals("]")) {
			read = true;
			return array;
		}
		
		JSON json = parse();
		array.add(json);
		
		if (read)
			nextLexeme();
		while (lexeme.equals(",")) {
			read = true;
			json = parse();
			array.add(json);
			if (read)
				nextLexeme();
		}
		
		read = true;
		if (!lexeme.equals("]"))
			throw new InvalidJSONFormat("expected ], found " + lexeme);
		return array;
	}
	
	private String nextString() {
		StringBuilder builder = new StringBuilder();
		nextLexeme();
		if (lexeme.equals("\""))
			return "";
		
		builder.append(lexeme);
		nextLexeme();
		if (lexeme.equals("\""))
			return builder.toString();
		
		while (!(lexeme.equals("\""))) {
			builder.append(" " + lexeme);
			nextLexeme();
		}
		
		return builder.toString();
	}
	
	private void nextLexeme() {
		if (read)
			read = false;
		
		if (!lexer.hasNext())
			return;
		
		lexeme = lexer.next();
	}

}
