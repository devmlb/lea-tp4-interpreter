package lea;

import java_cup.runtime.Symbol;

import lea.Node.*;

%%

%public
%class Lexer
%cupsym Terminal
%cup
%unicode
%line
%column

%{
  private Reporter reporter;

  public Lexer(java.io.Reader reader, Reporter reporter) {
    this(reader);
    this.reporter = reporter;
  }

  private String parseString(String s) {
    return s.subSequence(1, s.length()-1).toString()
      .replace("\\b", "\b")
      .replace("\\r", "\r")
      .replace("\\f", "\f")
      .replace("\\n", "\n")
      .replace("\\t", "\t")
      .replace("\\'", "'")
      .replace("\\\"", "\"")
      .replace("\\\\", "\\");
  }
  
  private Symbol mark(int terminal) {
    Reporter.Span span = new Reporter.Span(yyline+1, yycolumn+1, yylength());
  	return new Symbol(terminal, yyline+1, yycolumn+1, span);
  }

  private Symbol mark(int terminal, Node node) {
    Reporter.Span span = new Reporter.Span(yyline+1, yycolumn+1, yylength());
    reporter.attach(node, span);
  	return new Symbol(terminal, yyline+1, yycolumn+1, node);
  }

  private void error(String message) {
    reporter.error(Reporter.Phase.LEXER, new Reporter.Span(yyline+1, yycolumn+1, yylength()), message);
  }
  
%}

%%

 /* Keywords */
"algorithme"   				{ return mark(Terminal.ALGORITHME); }
"début"        				{ return mark(Terminal.DEBUT); }
"fin"        				{ return mark(Terminal.FIN); }

"si"        				{ return mark(Terminal.SI); }
"alors"        				{ return mark(Terminal.ALORS); }
"sinon"        				{ return mark(Terminal.SINON); }

"écrire"    				{ return mark(Terminal.ECRIRE); }

 /* Symbols */
"<-"        				{ return mark(Terminal.AFFECTATION); }
"("         				{ return mark(Terminal.PAR_G); }
")"         				{ return mark(Terminal.PAR_D); }
";"         				{ return mark(Terminal.PT_VIRG); }

"+"         				{ return mark(Terminal.PLUS); }
"-"         				{ return mark(Terminal.MOINS); }
"*"         				{ return mark(Terminal.MULTIPLIE); }
"="         				{ return mark(Terminal.EGAL); }
"<"         				{ return mark(Terminal.INFERIEUR); }
"et"         				{ return mark(Terminal.ET); }
"ou"         				{ return mark(Terminal.OU); }

 /* Booleans */
"vrai"	                	{ return mark(Terminal.LITERAL, new Bool(true)); }
"faux"			 			{ return mark(Terminal.LITERAL, new Bool(false)); }

 /* Integers */
0|[1-9][0-9]*		 		{ return mark(Terminal.LITERAL, new Int(Integer.parseInt(yytext()))); }

 /* Identifiers */
[A-Za-z][A-Za-z_0-9]*		{ return mark(Terminal.ID, new Identifier(yytext())); }

 /* Removal of characters that should not be parsed */
[ \t\n\r\f]					{ /* ignore white space. */ }
\/\/.*						{ /* One-line comments */ }
\/\*([^\*]|\*[^\/])*\*\/	{ /* Multi-line comments */ }

 /* Unexpected characters */
<<EOF>>						{ return new Symbol(Terminal.EOF); }
[^]           				{ error("Illegal character: " + yytext()); }
