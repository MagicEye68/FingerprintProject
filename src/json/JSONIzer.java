package json;

import java.io.File;
import java.io.InputStream;

public class JSONIzer {

	public static JSON parse(String s) {
		JSONLexer lexer = new JSONLexer(s);
		return new JSONSyntacticAnalyzer(lexer).parse();
	}
	
	public static JSON parse(InputStream is) {
		JSONLexer lexer = new JSONLexer(is);
		return new JSONSyntacticAnalyzer(lexer).parse();
	}
	
	public static JSON parse(File f) {
		JSONLexer lexer = new JSONLexer(f);
		return new JSONSyntacticAnalyzer(lexer).parse();
	}
	
	public static JSON jsonize(Object o) {
		try {
			return new JSONTransformer().transform(o);
		} catch (IllegalAccessException e) {
			throw new IllegalJsonConversion(e.getCause());
		}
	}

}
