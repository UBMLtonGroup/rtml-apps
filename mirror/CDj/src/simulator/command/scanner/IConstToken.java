package command.scanner;
import command.parser.TT;
//class for IConst tokens
public class IConstToken extends Yytoken
{
    //int value;
    //boolean isOctal;
    IConstToken(String matchedText, int startChar, int endChar)
    {
		super(Double.valueOf(matchedText),startChar, endChar, TT.ICONST);
		//this.value = Integer.parseInt(matchedText);
		//this.isOctal = isOctal;
	}
    //public int getValue() { return value; }
	//public String toString() { return "ICONST"; }
  
}
