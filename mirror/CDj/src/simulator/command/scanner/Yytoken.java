//Yytoken.java
package command.scanner;
import command.parser.TT;
public class Yytoken extends command.java_cup.runtime.Symbol
{
  
    //protected int tokenType;
    //protected String matchedText;
    //protected int lineNumber;
    protected String filename;
    

    public Yytoken(Object matched, int startChar, int endChar, int tokenSubType)
    {
	super(tokenSubType,startChar + 1, endChar + 1, matched);
		//this.matchedText = matchedText;
	//this.lineNumber = linenumber + 1;
	//this.tokenSubType = tokenSubType;
	//System.out.println("Found: " + toString());
    }
    //public int getTokenType() { return tokenType; }
    public String getMatchedText() { return (String)value; }
    public int getLineNumber() { return left; }
    public String getFileName() { return filename; }
   
    //Override
	public String toString()
	{
		String matchedText = String.valueOf(value);
		String type;
		switch(sym)
		    {
		    case TT.IDENT:
			type = "IDENT";
			break;
			case TT.ICONST:
			type = "ICONST";
			break;
		    case TT.FCONST:
			type = "FCONST";
			break;
		    case TT.COMMA:
			type = "COMMA";
			break;
		    case TT.SLASH:
			type = "SLASH";
			break;
		    case TT.LPAREN:
			type = "LPAREN";
			break;
		    case TT.MINUS:
			type = "MINUS";
			break;
		    case TT.MOD:
			type = "MOD";
			break;
		    case TT.PLUS:
			type = "PLUS";
			break;
		    case TT.RPAREN:
			type = "RPAREN";
			break;
		    case TT.STAR:
			type = "STAR";
			break;
		    case TT.EOF:
			type = "EOF";
			break;
			case TT.SIN:
				type = "SIN";
				break;
			case TT.COS:
				type = "COS";
				break;			case TT.TAN:
				type = "TAN";
				break;
			case TT.ASIN:
				type = "ASIN";
				break;
			case TT.ACOS:
				type = "ACOS";
				break;			case TT.ATAN:
				type = "ATAN";
				break;							case TT.error:
			type = "ERROR";
			break;			default:
			type = matchedText.toUpperCase();
		    }

		
		return "\"" + filename + "\" " + left + ": " + type + " (" + matchedText + ")";
	       }
/*
	public String toString()
    {
	switch(type)
	    {
	    case ABSTRACTION:
			return super.toString() + "ABSTRACTION (abstraction)";
	    case ARRAY:
			return super.toString() + "ARRAY (array)";
	    case BEGIN:
			return super.toString() +  "BEGIN (begin)";
	    case CHAR:
			return super.toString() + "CHAR (char)";
	    case CONT:
			return super.toString() + "CONT (cont)";
	    case END:
			return super.toString() + "END (end)";
	    case EXIT:
			return super.toString() + "EXIT (exit)";
	    case IT:
			return super.toString() + "IT (it)";
	    case FLOAT:
			return super.toString() + "FLOAT (float)";
	    case INT:
			return super.toString() + "INT (int)";
	    case OF:
			return super.toString() + "OF (of)";
	    case PROCEDURE:
			return super.toString() + "PROCEDURE (procedure)";
	    case TI:
			return super.toString() + "TI (ti)";
	    case TYPE:
			return super.toString() + "TYPE (type)";
	    case VAR:
			return super.toString() + "VAR (var)";
	    default:
	      return "invalid keyword";
	      //need to decide what to do here
	 }
    }
*/
    

}
