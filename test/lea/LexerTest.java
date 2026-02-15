package lea;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import java_cup.runtime.Symbol;
import lea.Reporter.Phase;

/**
 * JUnit tests for the Lexer class.
 */
public final class LexerTest {

	private static List<Symbol> analyse(String source, Reporter reporter) {
		var tokens = new ArrayList<Symbol>();
		try (Reader reader = new StringReader(source)) {
			var lexer = new Lexer(reader, reporter);
			while (!lexer.yyatEOF()) {
				tokens.add(lexer.next_token());
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return tokens;
	}

	private static void assertHasErrorContaining(String source, String fragment) {
		var reporter = new Reporter();
		analyse(source, reporter);
		boolean matches = reporter.getErrors(Phase.LEXER)
				.stream()
				.anyMatch(m -> m.contains(fragment));
		assertTrue(matches, () -> "Expected error containing: \"" + fragment + "\"");
	}

	private static void assertMatches(String source, int... terminals) {
		var reporter = new Reporter();
		var tokens = analyse(source, reporter);
		assertEquals(terminals.length, tokens.size()-1, "Token count mismatch" + tokens);
		for (int i = 0; i < terminals.length; i++) {
			assertEquals(terminals[i], tokens.get(i).sym, "Token mismatch at index " + i);
		}
	}

	/* =========================
	 * === IDENTIFIANTS / MOTS-CLES
	 * ========================= */

	@Test
	void identifier_basic() {
		assertMatches("x", Terminal.ID);
	}

	@Test
	void identifier_with_underscores_and_digits() {
		assertMatches("a_1_b2", Terminal.ID);
	}

	@Test
	void keyword_vs_identifier_prefix() {
		// "si" est un mot-clé, "sinon" est un mot-clé, "simon" est un identifiant
		assertMatches("si sinon simon", Terminal.SI, Terminal.SINON, Terminal.ID);
	}

	@Test
	void keywords_allCore() {
		assertMatches("""
			algorithme début fin
			si alors sinon
			écrire
			""",
			Terminal.ALGORITHME, Terminal.DEBUT, Terminal.FIN,
			Terminal.SI, Terminal.ALORS, Terminal.SINON,
			Terminal.ECRIRE
		);
	}

	/* =========================
	 * === SYMBOLES / OPERATEURS
	 * ========================= */

	@Test
	void punctuation_and_assignment() {
		assertMatches("<- ; ( )", Terminal.AFFECTATION, Terminal.PT_VIRG, Terminal.PAR_G, Terminal.PAR_D);
	}

	@Test
	void operators_and_logicals() {
		assertMatches("+ - * = < et ou",
			Terminal.PLUS, Terminal.MOINS, Terminal.MULTIPLIE,
			Terminal.EGAL, Terminal.INFERIEUR,
			Terminal.ET, Terminal.OU
		);
	}

	/* =========================
	 * === LITTERAUX
	 * ========================= */

	@Test
	void boolean_literals() {
		assertMatches("vrai faux", Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void integer_literals() {
		assertMatches("0 7 42 123456", Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void char_literals_simple_and_escaped() {
		assertMatches("'a' '\\n' '\\'' '\\\\'", Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL);
	}

	@Test
	void string_literals_simple_and_escaped() {
		assertMatches("\"abc\" \"a\\\\nb\" \"\\\"\" \"\\\\\"",
			Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL, Terminal.LITERAL
		);
	}

	/* =========================
	 * === ESPACES / COMMENTAIRES
	 * ========================= */

	@Test
	void whitespace_is_ignored() {
		assertMatches(" \n\t  x \r\f y ", Terminal.ID, Terminal.ID);
	}

	@Test
	void line_comment_is_ignored() {
		assertMatches("""
			x // comment
			y
			""", Terminal.ID, Terminal.ID);
	}

	@Test
	void block_comment_is_ignored() {
		assertMatches("x /* comment */ y", Terminal.ID, Terminal.ID);
	}

	@Test
	void block_comment_with_stars_is_ignored() {
		assertMatches("x /* ** */ y", Terminal.ID, Terminal.ID);
	}

	/* =========================
	 * === ERREURS LEXICALES
	 * ========================= */

	@Test
	void illegal_character_is_reported() {
		assertHasErrorContaining("@", "Illegal character");
	}

	@Test
	void illegal_character_does_not_prevent_other_tokens() {
		assertMatches("x @ y", Terminal.ID, Terminal.ID);
	}

	@Test
	void unexpected_dot_is_illegal() {
		assertHasErrorContaining(".", "Illegal character");
	}
}
