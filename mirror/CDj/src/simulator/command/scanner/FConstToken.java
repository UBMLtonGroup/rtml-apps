package command.scanner;
import command.parser.TT;

public class FConstToken extends Yytoken
{
    //float value;
    FConstToken(String matchedText, int startChar, int endChar)
    {
		super(Double.valueOf(matchedText),startChar,endChar,TT.FCONST);		//this.value = Float.valueOf(matchedText).floatValue();
	}
    //public float getValue() { return value; }
	//public String toString() { return "ICONST"; }
}
