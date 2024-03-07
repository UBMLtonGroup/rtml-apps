package command.scanner;
import command.parser.TT;

public class IdentToken extends Yytoken
{
    //String value;
    IdentToken(String matchedText, int startChar, int endChar)
    {	super(matchedText,startChar,endChar,TT.IDENT);
	//this.value = matchedText;
	}
    //public String getValue() { return value; }	//public String toString() { return "ICONST"; }
  

}
