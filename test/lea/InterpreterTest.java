package lea;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import lea.Node.*;
import lea.Reporter.Phase;

/**
 * JUnit tests for the Interpreter class.
 */
public final class InterpreterTest {

	private static Reporter analyse(String source) {
		Reporter reporter = new Reporter();
		try(Reader reader = new StringReader(source)) {
			var lexer = new Lexer(reader, reporter);
			var parser = new Parser(lexer,reporter);
			var interpreter = new Interpreter(reporter);
			Program program = parser.parseProgram();
			assertTrue(reporter.getErrors(Phase.LEXER).isEmpty(), () -> "Lexing errors:\n" + String.join("\n", reporter.getErrors(Phase.LEXER)));
			assertTrue(reporter.getErrors(Phase.PARSER).isEmpty(), () -> "Parsing errors:\n" + String.join("\n", reporter.getErrors(Phase.PARSER)));
			interpreter.interpret(program);
		} catch (Exception e) {
			fail(e);
		}
		return reporter;
	}

	private static void assertHasErrorContaining(String source, String fragment) {
		Reporter reporter = analyse(source);
		boolean matches = reporter.getErrors(Phase.RUNTIME)
				.stream()
				.anyMatch(m -> m.contains(fragment));
		assertTrue(matches,
				() -> "Expected runtime error containing: \"" + fragment + "\"\nGot:\n"
						+ String.join("\n", reporter.getErrors(Phase.RUNTIME)));
	}

	private static void assertWrites(String source, Value... expected) {
		Reporter reporter = analyse(source);
		var runtimeErrors = reporter.getErrors(Phase.RUNTIME);
		assertTrue(runtimeErrors.isEmpty(), () -> runtimeErrors.stream().reduce("", (x,y)->x+y+"\n"));
		var output = reporter.getOutput();
		assertEquals(expected.length, output.size(), "Output size mismatch: " + output);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], output.get(i), "Output mismatch at index " + i);
		}
	}

	/* =========================
	 * === NORMAL CASES =========
	 * ========================= */

	@Test
	void simpleAssignmentsAndExpressions() {
		String source = """
				algorithme
				variables
				  x : entier;
				  y : entier;
				début
				  x <- 1;
				  y <- x + 1;
				  écrire(y * 3);
				fin
				""";
		assertWrites(source, new Int(6));
	}

	@Test
	void ifThenElseExecution() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  si x = 0 alors
				    x <- 1;
				  sinon
				    x <- 2;
				  fin si
				  écrire(x);
				fin
				""";
		assertWrites(source, new Int(1));
	}

	@Test
	void nestedIf_executesCorrectBranch() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 2;
				  si x < 3 alors
				    si x = 2 alors
				      écrire(10);
				    sinon
				      écrire(11);
				    fin si
				  sinon
				    écrire(12);
				  fin si
				fin
				""";
		assertWrites(source, new Int(10));
	}

	@Test
	void whileLoopExecution() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 3 faire
				    x <- x + 1;
				  fin tant que
				  écrire(x);
				fin
				""";
		assertWrites(source, new Int(3));
	}

	@Test
	void whileLoop_zeroIterations_doesNotExecuteBody() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 0 faire
				    x <- 1;
				  fin tant que
				  écrire(x);
				fin
				""";
		assertWrites(source, new Int(0));
	}

	@Test
	void breakInsideLoop_exitsLoop() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 0;
				  tant que x < 10 faire
				    x <- x + 1;
				    si x = 3 alors
				      interrompre;
				    fin si
				  fin tant que
				  écrire(x);
				fin
				""";
		assertWrites(source, new Int(3));
	}

	@Test
	void forLoopIncreasing() {
		String source = """
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 1 à 5 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""";
		assertWrites(source, new Int(15));
	}

	@Test
	void forLoopWithStep() {
		String source = """
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 1 à 6 pas 2 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""";
		assertWrites(source, new Int(9));
	}
	
	@Test
	void forLoopDecreasing() {
		String source = """
				algorithme
				variables
				  i : entier;
				  s : entier;
				début
				  s <- 0;
				  pour i de 5 à 1 faire
				    s <- s + i;
				  fin pour
				  écrire(s);
				fin
				""";
		assertWrites(source, new Int(15));
	}

	@Test
	void forLoop_singleIteration_whenStartEqualsEnd() {
		String source = """
				algorithme
				variables
				  i : entier;
				début
				  pour i de 3 à 3 faire
				    écrire(i);
				  fin pour
				fin
				""";
		assertWrites(source, new Int(3));
	}


	@Test
	void arraysAndIndexing() {
		String source = """
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- [1, 2, 3];
				  a[2] <- 5;
				  écrire(a[2]);
				fin
				""";
		assertWrites(source, new Int(5));
	}

	@Test
	void stringsAndLength() {
		String source = """
				algorithme
				variables
				  s : chaîne;
				début
				  s <- "abc";
				  écrire(longueur(s));
				  écrire(s[2]);
				fin
				""";
		assertWrites(source, new Int(3));
	}

	@Test
	void write_multipleArguments_preservesOrder_andTypes() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 4;
				  écrire("x=", x, ", ok=", x = 4);
				fin
				""";
		assertWrites(source, new Int(4), new Bool(true));
	}

	@Test
	void precedenceAndAssociativity_areCorrect() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 1 + 2 * 3;
				  écrire(x);
				  x <- (1 + 2) * 3;
				  écrire(x);
				  x <- 10 - 3 - 2;
				  écrire(x);
				fin
				""";
		assertWrites(source, new Int(7), new Int(9), new Int(15));
	}

	@Test
	void booleanOperators_andComparison_work() {
		String source = """
				algorithme
				variables
				  x : entier;
				début
				  x <- 3;
				  écrire(x < 4);
				  écrire(x = 3);
				  écrire(x = 2);
				fin
				""";
		assertWrites(source, new Bool(true), new Bool(true), new Bool(false));
	}

	/* =========================
	 * ==== RUNTIME ERRORS ======
	 * ========================= */

	@Test
	void breakOutsideLoop_isReported() {
		String source = """
				algorithme
				variables
				début
				  interrompre;
				fin
				""";
		assertHasErrorContaining(source, "Interrompre ne peut pas être en dehors d'une boucle");
	}

	@Test
	void arrayIndexOutOfBounds_isReported() {
		String source = """
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- [1, 2];
				  écrire(a[3]);
				fin
				""";
		assertHasErrorContaining(source, "Indice hors limites");
	}

	@Test
	void stringIndexOutOfBounds_isReported() {
		String source = """
				algorithme
				variables
				  s : chaîne;
				début
				  s <- "ab";
				  écrire(s[3]);
				fin
				""";
		assertHasErrorContaining(source, "Indice hors limites");
	}

	@Test
	void invalidArraySize_isReported() {
		String source = """
				algorithme
				variables
				  a : tableau de entier;
				début
				  a <- tableau(-1, 0);
				fin
				""";
		assertHasErrorContaining(source, "Taille invalide");
	}

	@Test
	void infiniteForLoop_isReported_stepIsZero() {
		String source = """
				algorithme
				variables
				  i : entier;
				début
				  pour i de 1 à 5 pas 0 faire
				    écrire(i);
				  fin pour
				fin
				""";
		assertHasErrorContaining(source, "Boucle pour infinie");
	}

	@Test
	void infiniteForLoop_decreasingStepIsNonNegative_isReported() {
		String source = """
				algorithme
				variables
				  i : entier;
				début
				  pour i de 5 à 1 pas 1 faire
				    écrire(i);
				  fin pour
				fin
				""";
		assertHasErrorContaining(source, "Boucle pour infinie");
	}

	@Test
	void infiniteForLoop_decreasingStepIsZero_isReported() {
		String source = """
				algorithme
				variables
				  i : entier;
				début
				  pour i de 5 à 1 pas 0 faire
				    écrire(i);
				  fin pour
				fin
				""";
		assertHasErrorContaining(source, "Boucle pour infinie");
	}
}
