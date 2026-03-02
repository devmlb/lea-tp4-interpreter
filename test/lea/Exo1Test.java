package lea;

import org.junit.jupiter.api.Test;

import lea.Node.Int;
import lea.Reporter.Phase;

public final class Exo1Test {

	@Test
	void output_of_defaultProgram() {
		new LeaAsserts("""
				x <- 1;
				y <- x + 2;
				écrire(y * 3);
				""")
		.assertHasNoError()
		.assertOutputs(new Int(9));
	}

	@Test
	void output_of_q1c() {
		new LeaAsserts("""
				x <- 4;
				écrire(x);
				""")
		.assertHasNoError()
		.assertOutputs(new Int(4));
	}

	@Test
	void recovery_in_sequence() {
		new LeaAsserts("""
				x <- 1 + ;
				écrire(0);
				""")
		.assertHasErrorContaining(Phase.PARSER, "Erreur dans la commande");
	}

	@Test
	void recovery_inside_if_condition() {
		new LeaAsserts("""
				si 1 +  alors
				    écrire(1);
				sinon
				    écrire(0);
				fin si
				""")
		.assertHasErrorContaining(Phase.PARSER, "Erreur dans l'expression");
	}

	@Test
	void recovery_in_if_statement() {
		new LeaAsserts("""
				si vrai alors
				    écrire(0);
				fin si
				écrire(1);
				""")
		.assertHasErrorContaining(Phase.PARSER, "Erreur dans la conditionnelle");
	}
	
}