package command.scanner;

import command.java_cup.runtime.Symbol;
import command.parser.TT;

public class Yylex implements command.java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

	private int commentDepth;
	private int commentStartLine;
	private int stringStartLine; // for strings and comments

	private void errorMsg(String msg) {
		System.out.println(msg + " (" + yytext() + ") at character " + yychar + ".");
	}

	private Symbol makeToken(int type) {
		return new Yytoken(yytext(), yychar, yychar + yylength() - 1, type);
	}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Yylex(java.io.Reader reader) {
		this();
		if (null == reader) { throw (new Error("Error: Bad input stream initializer.")); }
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Yylex(java.io.InputStream instream) {
		this();
		if (null == instream) { throw (new Error("Error: Bad input stream initializer.")); }
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex() {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

		commentDepth = 0;
	}

	private boolean yy_eof_done = false;

	private void yy_do_eof() {
		if (false == yy_eof_done) {

			switch (yy_lexical_state) {
			case COMMENT:
				// int newLineIndex = yytext().indexOf("\n");
				System.out.println((commentStartLine + 1) + ": unterminated comment");
				// System.out.print("\"" + filename + "\" " + (commentStartLine + 1) + ":
				// unterminated comment: " + "(");
				/*
				 * if(newLineIndex != -1) { System.out.println(yytext().substring(newLineIndex) +
				 * ")"); } else { System.out.println(yytext() + ")"); }
				 */
				break;
			default:
				break;
			}
		}
		yy_eof_done = true;
	}
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = { 0, 101 };

	private void yybegin(int state) {
		yy_lexical_state = state;
	}

	private int yy_advance() throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) { return yy_buffer[yy_buffer_index++]; }

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer, yy_buffer_read, yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) { return YY_EOF; }
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer, yy_buffer_read, yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) { return YY_EOF; }
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}

	private void yy_move_end() {
		if (yy_buffer_end > yy_buffer_start && '\n' == yy_buffer[yy_buffer_end - 1]) yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start && '\r' == yy_buffer[yy_buffer_end - 1]) yy_buffer_end--;
	}
	private boolean yy_last_was_cr = false;

	private void yy_mark_start() {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr = true;
			} else yy_last_was_cr = false;
		}
		yychar = yychar + yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}

	private void yy_mark_end() {
		yy_buffer_end = yy_buffer_index;
	}

	private void yy_to_mark() {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start)
				&& ('\r' == yy_buffer[yy_buffer_end - 1] || '\n' == yy_buffer[yy_buffer_end - 1]
						|| 2028/* LS */== yy_buffer[yy_buffer_end - 1] || 2029/* PS */== yy_buffer[yy_buffer_end - 1]);
	}

	private java.lang.String yytext() {
		return (new java.lang.String(yy_buffer, yy_buffer_start, yy_buffer_end - yy_buffer_start));
	}

	private int yylength() {
		return yy_buffer_end - yy_buffer_start;
	}

	private char[] yy_double(char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2 * buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = { "Error: Internal error.\n", "Error: Unmatched input.\n" };

	private void yy_error(int code, boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) { throw new Error("Fatal Error.\n"); }
	}

	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i = 0; i < size1; i++) {
			for (int j = 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex == -1) ? st : st.substring(0, commaIndex);
				st = st.substring(commaIndex + 1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j] = Integer.parseInt(workString);
					continue;
				}
				lengthString = workString.substring(colonIndex + 1);
				sequenceLength = Integer.parseInt(lengthString);
				workString = workString.substring(0, colonIndex);
				sequenceInteger = Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
	/* 0 */YY_NOT_ACCEPT,
	/* 1 */YY_NO_ANCHOR,
	/* 2 */YY_NO_ANCHOR,
	/* 3 */YY_NO_ANCHOR,
	/* 4 */YY_NO_ANCHOR,
	/* 5 */YY_NO_ANCHOR,
	/* 6 */YY_NO_ANCHOR,
	/* 7 */YY_NO_ANCHOR,
	/* 8 */YY_NO_ANCHOR,
	/* 9 */YY_NO_ANCHOR,
	/* 10 */YY_NO_ANCHOR,
	/* 11 */YY_NO_ANCHOR,
	/* 12 */YY_NO_ANCHOR,
	/* 13 */YY_NO_ANCHOR,
	/* 14 */YY_NO_ANCHOR,
	/* 15 */YY_NO_ANCHOR,
	/* 16 */YY_NO_ANCHOR,
	/* 17 */YY_NO_ANCHOR,
	/* 18 */YY_NO_ANCHOR,
	/* 19 */YY_NO_ANCHOR,
	/* 20 */YY_NO_ANCHOR,
	/* 21 */YY_NO_ANCHOR,
	/* 22 */YY_NO_ANCHOR,
	/* 23 */YY_NO_ANCHOR,
	/* 24 */YY_NO_ANCHOR,
	/* 25 */YY_NO_ANCHOR,
	/* 26 */YY_NO_ANCHOR,
	/* 27 */YY_NO_ANCHOR,
	/* 28 */YY_NO_ANCHOR,
	/* 29 */YY_NO_ANCHOR,
	/* 30 */YY_NO_ANCHOR,
	/* 31 */YY_NO_ANCHOR,
	/* 32 */YY_NO_ANCHOR,
	/* 33 */YY_NO_ANCHOR,
	/* 34 */YY_NO_ANCHOR,
	/* 35 */YY_NO_ANCHOR,
	/* 36 */YY_NO_ANCHOR,
	/* 37 */YY_NO_ANCHOR,
	/* 38 */YY_NO_ANCHOR,
	/* 39 */YY_END,
	/* 40 */YY_NO_ANCHOR,
	/* 41 */YY_NO_ANCHOR,
	/* 42 */YY_NO_ANCHOR,
	/* 43 */YY_NO_ANCHOR,
	/* 44 */YY_NO_ANCHOR,
	/* 45 */YY_NO_ANCHOR,
	/* 46 */YY_NO_ANCHOR,
	/* 47 */YY_NO_ANCHOR,
	/* 48 */YY_NO_ANCHOR,
	/* 49 */YY_NO_ANCHOR,
	/* 50 */YY_NO_ANCHOR,
	/* 51 */YY_NO_ANCHOR,
	/* 52 */YY_NO_ANCHOR,
	/* 53 */YY_NO_ANCHOR,
	/* 54 */YY_NO_ANCHOR,
	/* 55 */YY_NO_ANCHOR,
	/* 56 */YY_NO_ANCHOR,
	/* 57 */YY_NO_ANCHOR,
	/* 58 */YY_NO_ANCHOR,
	/* 59 */YY_NO_ANCHOR,
	/* 60 */YY_NO_ANCHOR,
	/* 61 */YY_NO_ANCHOR,
	/* 62 */YY_NO_ANCHOR,
	/* 63 */YY_NO_ANCHOR,
	/* 64 */YY_NO_ANCHOR,
	/* 65 */YY_NO_ANCHOR,
	/* 66 */YY_NO_ANCHOR,
	/* 67 */YY_NO_ANCHOR,
	/* 68 */YY_NO_ANCHOR,
	/* 69 */YY_NO_ANCHOR,
	/* 70 */YY_NO_ANCHOR,
	/* 71 */YY_NO_ANCHOR,
	/* 72 */YY_NO_ANCHOR,
	/* 73 */YY_NO_ANCHOR,
	/* 74 */YY_NO_ANCHOR,
	/* 75 */YY_NO_ANCHOR,
	/* 76 */YY_NO_ANCHOR,
	/* 77 */YY_NO_ANCHOR,
	/* 78 */YY_NO_ANCHOR,
	/* 79 */YY_NOT_ACCEPT,
	/* 80 */YY_NO_ANCHOR,
	/* 81 */YY_NO_ANCHOR,
	/* 82 */YY_END,
	/* 83 */YY_NO_ANCHOR,
	/* 84 */YY_NOT_ACCEPT,
	/* 85 */YY_NO_ANCHOR,
	/* 86 */YY_NO_ANCHOR,
	/* 87 */YY_NOT_ACCEPT,
	/* 88 */YY_NO_ANCHOR,
	/* 89 */YY_NOT_ACCEPT,
	/* 90 */YY_NO_ANCHOR,
	/* 91 */YY_NOT_ACCEPT,
	/* 92 */YY_NO_ANCHOR,
	/* 93 */YY_NOT_ACCEPT,
	/* 94 */YY_NO_ANCHOR,
	/* 95 */YY_NOT_ACCEPT,
	/* 96 */YY_NO_ANCHOR,
	/* 97 */YY_NOT_ACCEPT,
	/* 98 */YY_NO_ANCHOR,
	/* 99 */YY_NOT_ACCEPT,
	/* 100 */YY_NO_ANCHOR,
	/* 101 */YY_NOT_ACCEPT,
	/* 102 */YY_NO_ANCHOR,
	/* 103 */YY_NO_ANCHOR,
	/* 104 */YY_NO_ANCHOR,
	/* 105 */YY_NO_ANCHOR,
	/* 106 */YY_NO_ANCHOR,
	/* 107 */YY_NO_ANCHOR,
	/* 108 */YY_NO_ANCHOR,
	/* 109 */YY_NO_ANCHOR,
	/* 110 */YY_NO_ANCHOR,
	/* 111 */YY_NO_ANCHOR,
	/* 112 */YY_NO_ANCHOR,
	/* 113 */YY_NO_ANCHOR,
	/* 114 */YY_NO_ANCHOR,
	/* 115 */YY_NO_ANCHOR,
	/* 116 */YY_NO_ANCHOR,
	/* 117 */YY_NO_ANCHOR,
	/* 118 */YY_NO_ANCHOR,
	/* 119 */YY_NO_ANCHOR,
	/* 120 */YY_NO_ANCHOR,
	/* 121 */YY_NO_ANCHOR,
	/* 122 */YY_NO_ANCHOR,
	/* 123 */YY_NO_ANCHOR,
	/* 124 */YY_NO_ANCHOR,
	/* 125 */YY_NO_ANCHOR,
	/* 126 */YY_NO_ANCHOR,
	/* 127 */YY_NO_ANCHOR,
	/* 128 */YY_NO_ANCHOR,
	/* 129 */YY_NO_ANCHOR,
	/* 130 */YY_NO_ANCHOR,
	/* 131 */YY_NO_ANCHOR,
	/* 132 */YY_NO_ANCHOR,
	/* 133 */YY_NO_ANCHOR,
	/* 134 */YY_NO_ANCHOR,
	/* 135 */YY_NO_ANCHOR,
	/* 136 */YY_NO_ANCHOR,
	/* 137 */YY_NO_ANCHOR,
	/* 138 */YY_NO_ANCHOR,
	/* 139 */YY_NO_ANCHOR,
	/* 140 */YY_NO_ANCHOR,
	/* 141 */YY_NO_ANCHOR,
	/* 142 */YY_NO_ANCHOR,
	/* 143 */YY_NO_ANCHOR,
	/* 144 */YY_NO_ANCHOR,
	/* 145 */YY_NO_ANCHOR,
	/* 146 */YY_NO_ANCHOR,
	/* 147 */YY_NO_ANCHOR,
	/* 148 */YY_NO_ANCHOR,
	/* 149 */YY_NO_ANCHOR,
	/* 150 */YY_NO_ANCHOR,
	/* 151 */YY_NO_ANCHOR,
	/* 152 */YY_NO_ANCHOR,
	/* 153 */YY_NO_ANCHOR,
	/* 154 */YY_NO_ANCHOR,
	/* 155 */YY_NO_ANCHOR,
	/* 156 */YY_NO_ANCHOR,
	/* 157 */YY_NO_ANCHOR,
	/* 158 */YY_NO_ANCHOR,
	/* 159 */YY_NO_ANCHOR,
	/* 160 */YY_NO_ANCHOR,
	/* 161 */YY_NO_ANCHOR,
	/* 162 */YY_NO_ANCHOR,
	/* 163 */YY_NO_ANCHOR,
	/* 164 */YY_NO_ANCHOR,
	/* 165 */YY_NO_ANCHOR,
	/* 166 */YY_NO_ANCHOR,
	/* 167 */YY_NO_ANCHOR,
	/* 168 */YY_NO_ANCHOR,
	/* 169 */YY_NO_ANCHOR,
	/* 170 */YY_NO_ANCHOR,
	/* 171 */YY_NO_ANCHOR,
	/* 172 */YY_NO_ANCHOR,
	/* 173 */YY_NO_ANCHOR,
	/* 174 */YY_NO_ANCHOR,
	/* 175 */YY_NO_ANCHOR,
	/* 176 */YY_NO_ANCHOR,
	/* 177 */YY_NO_ANCHOR,
	/* 178 */YY_NO_ANCHOR,
	/* 179 */YY_NO_ANCHOR,
	/* 180 */YY_NO_ANCHOR,
	/* 181 */YY_NO_ANCHOR,
	/* 182 */YY_NO_ANCHOR,
	/* 183 */YY_NO_ANCHOR,
	/* 184 */YY_NO_ANCHOR };
	private int yy_cmap[] = unpackFromString(1, 130,
			"59:9,63,60,59,63,61,59:18,63,38,59:3,50,59:2,49,53,54,51,47,40,57,48,55:10,"
					+ "59,39,59,46,41,59:2,19,56,23,14,58,22,56:2,15,56:2,18,56,26,25,17,56,4,16,2"
					+ "4,56:4,20,56,42,59,43,52,21,59,10,37,9,8,7,1,36,35,30,56,27,3,32,28,5,31,33"
					+ ",13,11,12,2,34,6,56:2,29,44,59,45,59:2,0,62")[0];

	private int yy_rmap[] = unpackFromString(1, 185,
			"0,1,2,1:3,3,1:6,4,1:6,5,6,7,1:2,8,9,7,10,11,12,7:2,13,7:2,14,15,16,1,17,18,"
					+ "7:5,19,20,21,22,23,24,7:5,25,26,7:7,27,7:8,1:3,28,29,30,31,32,33,34,35,17,3"
					+ "6,37,38,39,40,25,41,42,43,26,44,27,45,46,47,48,49,50,51,52,53,54,55,56,57,5"
					+ "8,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,8"
					+ "3,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,1"
					+ "06,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,"
					+ "125,7,126,127,128")[0];

	private int yy_nxt[][] = unpackFromString(129, 64,
			"1,2,179,80,181:3,141,182,121,122,123,124,181,183,181:6,3,181:8,125,142,181:"
					+ "6,4,5,6,3,7,8,9,10,11,12,13,14,15,16,17,18,19,20,181,81,181,3,21:2,1,21,-1:"
					+ "65,181,184,181:7,143,181:2,144,181:24,-1:17,181:2,-1,181,-1:46,23,-1:70,79,"
					+ "-1:5,24,-1:16,84,-1:47,20,-1,25,84,-1:65,21:2,-1,21,-1,181:37,-1:17,181:2,-"
					+ "1,181,-1:12,89,-1:47,41,-1:2,89,-1:12,91,-1:47,26,-1:2,91,-1:6,181:34,44,18"
					+ "1:2,-1:17,181:2,-1,181,-1:6,181:34,45,181:2,-1:17,181:2,-1,181,-1:6,181:34,"
					+ "46,181:2,-1:17,181:2,-1,181,-1:6,181:34,53,181:2,-1:17,181:2,-1,181,-1:6,18"
					+ "1:34,54,181:2,-1:17,181:2,-1,181,-1:6,181:34,55,181:2,-1:17,181:2,-1,181,-1"
					+ ":6,181:6,158,181:30,-1:17,181:2,-1,181,-1:60,40,-1:15,95,-1:47,41,-1:2,95,-"
					+ "1:6,181:34,61,181:2,-1:17,181:2,-1,181,-1:6,181:34,62,181:2,-1:17,181:2,-1,"
					+ "181,-1:6,181:34,63,181:2,-1:17,181:2,-1,181,-1:6,181:34,64,181:2,-1:17,181:"
					+ "2,-1,181,-1:6,181:34,65,181:2,-1:17,181:2,-1,181,-1:6,181:34,66,181:2,-1:17"
					+ ",181:2,-1,181,-1:60,58,-1:63,59,-1:63,67,-1:9,79:59,39,82,39,79,-1,181:4,85"
					+ ",181:22,22,181:9,-1:17,181:2,-1,181,-1:60,26,-1:68,39,-1:57,77,-1:49,87,-1:"
					+ "10,87,-1:3,40,-1:9,181:8,103,181:26,27,181,-1:17,181:2,-1,181,-1:53,78,-1:1"
					+ "6,181:10,28,29,181:25,-1:17,181:2,-1,181,-1:45,93,-1:10,93,-1:3,58,-1:9,181"
					+ ":8,30,181:28,-1:17,181:2,-1,181,-1:45,97,-1:10,97,-1:3,59,-1:9,181:10,31,18"
					+ "1:26,-1:17,181:2,-1,181,-1:6,181:31,32,181:5,-1:17,181:2,-1,181,-1:45,99,-1"
					+ ":10,99,-1:3,67,-1:9,181:8,33,181:2,34,181:20,35,181:4,-1:17,181:2,-1,181,-1"
					+ ":6,181:27,36,181:9,-1:17,181:2,-1,181,-1:6,181:27,37,181:9,-1:17,181:2,-1,1"
					+ "81,-1:5,1,76:47,83,76:5,86,76:5,21:2,1,21,-1,181:11,38,181:25,-1:17,181:2,-"
					+ "1,181,-1:6,181:26,42,181:10,-1:17,181:2,-1,181,-1:6,181:2,43,181:34,-1:17,1"
					+ "81:2,-1,181,-1:6,181:10,47,48,181:25,-1:17,181:2,-1,181,-1:6,181:8,49,181:2"
					+ "8,-1:17,181:2,-1,181,-1:6,181:8,50,181:28,-1:17,181:2,-1,181,-1:6,181:27,51"
					+ ",181:9,-1:17,181:2,-1,181,-1:6,181:27,52,181:9,-1:17,181:2,-1,181,-1:6,181:"
					+ "6,56,181:30,-1:17,181:2,-1,181,-1:6,181:7,57,181:29,-1:17,181:2,-1,181,-1:6"
					+ ",181:6,60,181:30,-1:17,181:2,-1,181,-1:6,181:6,68,181:30,-1:17,181:2,-1,181"
					+ ",-1:6,181:26,69,181:10,-1:17,181:2,-1,181,-1:6,181:6,70,181:30,-1:17,181:2,"
					+ "-1,181,-1:6,181:6,71,181:30,-1:17,181:2,-1,181,-1:6,181:6,72,181:30,-1:17,1"
					+ "81:2,-1,181,-1:6,181:6,73,181:30,-1:17,181:2,-1,181,-1:6,181:6,74,181:30,-1"
					+ ":17,181:2,-1,181,-1:6,181:15,75,181:21,-1:17,181:2,-1,181,-1:6,181:4,88,181"
					+ ":5,90,181:26,-1:17,181:2,-1,181,-1:6,181:8,127,181,128,129,181:24,92,-1:17,"
					+ "181:2,-1,181,-1:6,181,94,181:4,96,181:22,98,181:7,-1:17,181:2,-1,181,-1:6,1"
					+ "81:9,100,181:2,130,181:24,-1:17,181:2,-1,181,-1:6,181:27,102,181:9,-1:17,18"
					+ "1:2,-1,181,-1:6,181:9,104,181:27,-1:17,181:2,-1,181,-1:6,181:4,105,181:5,10"
					+ "6,181:26,-1:17,181:2,-1,181,-1:6,181:6,107,181:22,108,181:7,-1:17,181:2,-1,"
					+ "181,-1:6,181:9,109,181:27,-1:17,181:2,-1,181,-1:6,181,110,181:35,-1:17,181:"
					+ "2,-1,181,-1:6,181:4,111,181:32,-1:17,181:2,-1,181,-1:6,181:10,112,181:26,-1"
					+ ":17,181:2,-1,181,-1:6,181:28,113,181:8,-1:17,181:2,-1,181,-1:6,181:8,114,18"
					+ "1:28,-1:17,181:2,-1,181,-1:6,181:27,115,181:9,-1:17,181:2,-1,181,-1:6,181:2"
					+ "8,116,181:8,-1:17,181:2,-1,181,-1:6,181:27,117,181:9,-1:17,181:2,-1,181,-1:"
					+ "6,181:11,118,181:25,-1:17,181:2,-1,181,-1:6,181:8,119,181:28,-1:17,181:2,-1"
					+ ",181,-1:6,181:25,120,181:11,-1:17,181:2,-1,181,-1:6,181:33,126,181:3,-1:17,"
					+ "181:2,-1,181,-1:6,181:12,131,181:24,-1:17,181:2,-1,181,-1:6,181:2,132,181:3"
					+ "4,-1:17,181:2,-1,181,-1:6,181:6,149,181:30,-1:17,181:2,-1,181,-1:6,150,181,"
					+ "151,181:4,152,181:29,-1:17,181:2,-1,181,-1:6,153,181:36,-1:17,181:2,-1,181,"
					+ "-1:6,181:15,154,181:21,-1:17,181:2,-1,181,-1:6,181:2,155,181:34,-1:17,181:2"
					+ ",-1,181,-1:6,181:6,133,181:30,-1:17,181:2,-1,181,-1:6,181:12,180,181:24,-1:"
					+ "17,181:2,-1,181,-1:6,181:4,134,181:32,-1:17,181:2,-1,181,-1:6,181:6,156,181"
					+ ":30,-1:17,181:2,-1,181,-1:6,181:29,135,181:7,-1:17,181:2,-1,181,-1:6,181:16"
					+ ",157,181:20,-1:17,181:2,-1,181,-1:6,181:3,159,181:33,-1:17,181:2,-1,181,-1:"
					+ "6,161,181:36,-1:17,181:2,-1,181,-1:6,181:17,162,181:19,-1:17,181:2,-1,181,-"
					+ "1:6,181:35,163,181,-1:17,181:2,-1,181,-1:6,181:4,164,181:32,-1:17,181:2,-1,"
					+ "181,-1:6,181:6,136,181:30,-1:17,181:2,-1,181,-1:6,181:29,137,181:7,-1:17,18"
					+ "1:2,-1,181,-1:6,181:18,165,181:18,-1:17,181:2,-1,181,-1:6,181:12,166,181:24"
					+ ",-1:17,181:2,-1,181,-1:6,181:5,167,181:31,-1:17,181:2,-1,181,-1:6,181:19,16"
					+ "8,181:17,-1:17,181:2,-1,181,-1:6,181:9,138,181:27,-1:17,181:2,-1,181,-1:6,1"
					+ "81:3,169,181:33,-1:17,181:2,-1,181,-1:6,181:20,170,181:16,-1:17,181:2,-1,18"
					+ "1,-1:6,181:6,171,181:30,-1:17,181:2,-1,181,-1:6,181:21,172,181:15,-1:17,181"
					+ ":2,-1,181,-1:6,181:7,173,181:29,-1:17,181:2,-1,181,-1:6,181:3,174,181:33,-1"
					+ ":17,181:2,-1,181,-1:6,181,139,181:35,-1:17,181:2,-1,181,-1:6,181:18,175,181"
					+ ":18,-1:17,181:2,-1,181,-1:6,181:22,176,181:14,-1:17,181:2,-1,181,-1:6,181:2"
					+ "3,177,181:13,-1:17,181:2,-1,181,-1:6,181:14,178,181:22,-1:17,181:2,-1,181,-"
					+ "1:6,181:24,140,181:12,-1:17,181:2,-1,181,-1:6,181:27,145,181:9,-1:17,181:2,"
					+ "-1,181,-1:6,181:6,160,181:30,-1:17,181:2,-1,181,-1:6,181:6,146,181:30,-1:17"
					+ ",181:2,-1,181,-1:6,181:14,147,181:22,-1:17,181:2,-1,181,-1:6,181:2,148,181:"
					+ "34,-1:17,181:2,-1,181,-1:5");

	public command.java_cup.runtime.Symbol next_token() throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol)
				yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				yy_do_eof();

				return new Yytoken("", yychar, yychar + yylength() - 1, TT.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			} else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				} else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:

					case -2:
						break;
					case 2: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -3:
						break;
					case 3: {
						errorMsg("unexpected character");
					}
					case -4:
						break;
					case 4: {
						return makeToken(TT.EXCL);
					}
					case -5:
						break;
					case 5: {
						return makeToken(TT.SEMI);
					}
					case -6:
						break;
					case 6: {
						return makeToken(TT.MINUS);
					}
					case -7:
						break;
					case 7: {
						return makeToken(TT.LBRACK);
					}
					case -8:
						break;
					case 8: {
						return makeToken(TT.RBRACK);
					}
					case -9:
						break;
					case 9: {
						return makeToken(TT.LBRACE);
					}
					case -10:
						break;
					case 10: {
						return makeToken(TT.RBRACE);
					}
					case -11:
						break;
					case 11: {
						return makeToken(TT.EQ);
					}
					case -12:
						break;
					case 12: {
						return makeToken(TT.COMMA);
					}
					case -13:
						break;
					case 13: {
						return makeToken(TT.SLASH);
					}
					case -14:
						break;
					case 14: {
						return makeToken(TT.LPAREN);
					}
					case -15:
						break;
					case 15: {
						return makeToken(TT.MOD);
					}
					case -16:
						break;
					case 16: {
						return makeToken(TT.PLUS);
					}
					case -17:
						break;
					case 17: {
						return makeToken(TT.CARET);
					}
					case -18:
						break;
					case 18: {
						return makeToken(TT.RPAREN);
					}
					case -19:
						break;
					case 19: {
						return makeToken(TT.STAR);
					}
					case -20:
						break;
					case 20: {
						return new IConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -21:
						break;
					case 21: {}
					case -22:
						break;
					case 22: {
						return makeToken(TT.LN);
					}
					case -23:
						break;
					case 23: {
						return makeToken(TT.ARROW);
					}
					case -24:
						break;
					case 24: {
						commentStartLine = yyline;
						commentDepth++;
						yybegin(COMMENT);
					}
					case -25:
						break;
					case 25: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -26:
						break;
					case 26: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -27:
						break;
					case 27: {
						return makeToken(TT.LOG);
					}
					case -28:
						break;
					case 28: {
						return makeToken(TT.COS);
					}
					case -29:
						break;
					case 29: {
						return makeToken(TT.COT);
					}
					case -30:
						break;
					case 30: {
						return makeToken(TT.CSC);
					}
					case -31:
						break;
					case 31: {
						return makeToken(TT.ABS);
					}
					case -32:
						break;
					case 32: {
						return makeToken(TT.SUM);
					}
					case -33:
						break;
					case 33: {
						return makeToken(TT.SEC);
					}
					case -34:
						break;
					case 34: {
						return makeToken(TT.SET);
					}
					case -35:
						break;
					case 35: {
						return makeToken(TT.SEQ);
					}
					case -36:
						break;
					case 36: {
						return makeToken(TT.SIN);
					}
					case -37:
						break;
					case 37: {
						return makeToken(TT.TAN);
					}
					case -38:
						break;
					case 38: {
						return makeToken(TT.INTEGRATE);
					}
					case -39:
						break;
					case 39: {}
					case -40:
						break;
					case 40: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -41:
						break;
					case 41: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -42:
						break;
					case 42: {
						return makeToken(TT.LOCK);
					}
					case -43:
						break;
					case 43: {
						return makeToken(TT.EVAL);
					}
					case -44:
						break;
					case 44: {
						return makeToken(TT.COSH);
					}
					case -45:
						break;
					case 45: {
						return makeToken(TT.COTH);
					}
					case -46:
						break;
					case 46: {
						return makeToken(TT.CSCH);
					}
					case -47:
						break;
					case 47: {
						return makeToken(TT.ACOS);
					}
					case -48:
						break;
					case 48: {
						return makeToken(TT.ACOT);
					}
					case -49:
						break;
					case 49: {
						return makeToken(TT.ACSC);
					}
					case -50:
						break;
					case 50: {
						return makeToken(TT.ASEC);
					}
					case -51:
						break;
					case 51: {
						return makeToken(TT.ASIN);
					}
					case -52:
						break;
					case 52: {
						return makeToken(TT.ATAN);
					}
					case -53:
						break;
					case 53: {
						return makeToken(TT.SECH);
					}
					case -54:
						break;
					case 54: {
						return makeToken(TT.SINH);
					}
					case -55:
						break;
					case 55: {
						return makeToken(TT.TANH);
					}
					case -56:
						break;
					case 56: {
						return makeToken(TT.TRUE);
					}
					case -57:
						break;
					case 57: {
						return makeToken(TT.PROD);
					}
					case -58:
						break;
					case 58: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -59:
						break;
					case 59: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -60:
						break;
					case 60: {
						return makeToken(TT.FALSE);
					}
					case -61:
						break;
					case 61: {
						return makeToken(TT.ACOSH);
					}
					case -62:
						break;
					case 62: {
						return makeToken(TT.ACOTH);
					}
					case -63:
						break;
					case 63: {
						return makeToken(TT.ACSCH);
					}
					case -64:
						break;
					case 64: {
						return makeToken(TT.ASECH);
					}
					case -65:
						break;
					case 65: {
						return makeToken(TT.ASINH);
					}
					case -66:
						break;
					case 66: {
						return makeToken(TT.ATANH);
					}
					case -67:
						break;
					case 67: {
						return new FConstToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -68:
						break;
					case 68: {
						return makeToken(TT.FREEZE);
					}
					case -69:
						break;
					case 69: {
						return makeToken(TT.UNLOCK);
					}
					case -70:
						break;
					case 70: {
						return makeToken(TT.DEFINE);
					}
					case -71:
						break;
					case 71: {
						return makeToken(TT.UNFREEZE);
					}
					case -72:
						break;
					case 72: {
						return makeToken(TT.UNDEFINE);
					}
					case -73:
						break;
					case 73: {
						return makeToken(TT.INTEGRATE);
					}
					case -74:
						break;
					case 74: {
						return makeToken(TT.FULL_ROW_REDUCE);
					}
					case -75:
						break;
					case 75: {
						return makeToken(TT.DISPLAY_FRACTIONS);
					}
					case -76:
						break;
					case 76: {}
					case -77:
						break;
					case 77: {
						commentDepth++;
					}
					case -78:
						break;
					case 78: {
						commentDepth--;
						if (commentDepth == 0) yybegin(YYINITIAL);
					}
					case -79:
						break;
					case 80: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -80:
						break;
					case 81: {
						errorMsg("unexpected character");
					}
					case -81:
						break;
					case 82: {}
					case -82:
						break;
					case 83: {}
					case -83:
						break;
					case 85: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -84:
						break;
					case 86: {}
					case -85:
						break;
					case 88: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -86:
						break;
					case 90: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -87:
						break;
					case 92: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -88:
						break;
					case 94: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -89:
						break;
					case 96: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -90:
						break;
					case 98: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -91:
						break;
					case 100: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -92:
						break;
					case 102: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -93:
						break;
					case 103: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -94:
						break;
					case 104: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -95:
						break;
					case 105: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -96:
						break;
					case 106: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -97:
						break;
					case 107: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -98:
						break;
					case 108: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -99:
						break;
					case 109: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -100:
						break;
					case 110: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -101:
						break;
					case 111: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -102:
						break;
					case 112: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -103:
						break;
					case 113: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -104:
						break;
					case 114: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -105:
						break;
					case 115: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -106:
						break;
					case 116: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -107:
						break;
					case 117: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -108:
						break;
					case 118: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -109:
						break;
					case 119: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -110:
						break;
					case 120: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -111:
						break;
					case 121: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -112:
						break;
					case 122: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -113:
						break;
					case 123: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -114:
						break;
					case 124: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -115:
						break;
					case 125: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -116:
						break;
					case 126: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -117:
						break;
					case 127: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -118:
						break;
					case 128: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -119:
						break;
					case 129: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -120:
						break;
					case 130: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -121:
						break;
					case 131: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -122:
						break;
					case 132: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -123:
						break;
					case 133: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -124:
						break;
					case 134: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -125:
						break;
					case 135: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -126:
						break;
					case 136: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -127:
						break;
					case 137: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -128:
						break;
					case 138: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -129:
						break;
					case 139: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -130:
						break;
					case 140: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -131:
						break;
					case 141: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -132:
						break;
					case 142: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -133:
						break;
					case 143: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -134:
						break;
					case 144: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -135:
						break;
					case 145: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -136:
						break;
					case 146: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -137:
						break;
					case 147: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -138:
						break;
					case 148: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -139:
						break;
					case 149: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -140:
						break;
					case 150: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -141:
						break;
					case 151: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -142:
						break;
					case 152: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -143:
						break;
					case 153: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -144:
						break;
					case 154: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -145:
						break;
					case 155: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -146:
						break;
					case 156: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -147:
						break;
					case 157: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -148:
						break;
					case 158: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -149:
						break;
					case 159: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -150:
						break;
					case 160: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -151:
						break;
					case 161: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -152:
						break;
					case 162: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -153:
						break;
					case 163: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -154:
						break;
					case 164: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -155:
						break;
					case 165: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -156:
						break;
					case 166: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -157:
						break;
					case 167: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -158:
						break;
					case 168: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -159:
						break;
					case 169: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -160:
						break;
					case 170: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -161:
						break;
					case 171: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -162:
						break;
					case 172: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -163:
						break;
					case 173: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -164:
						break;
					case 174: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -165:
						break;
					case 175: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -166:
						break;
					case 176: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -167:
						break;
					case 177: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -168:
						break;
					case 178: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -169:
						break;
					case 179: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -170:
						break;
					case 180: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -171:
						break;
					case 181: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -172:
						break;
					case 182: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -173:
						break;
					case 183: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -174:
						break;
					case 184: {
						return new IdentToken(yytext(), yychar, yychar + yylength() - 1);
					}
					case -175:
						break;
					default:
						yy_error(YY_E_INTERNAL, false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
