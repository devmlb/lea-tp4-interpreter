package lea;

import org.junit.jupiter.api.Test;
import lea.Node.*;
import lea.Reporter.Phase;

public final class Exo2Test {

	/* =========================
	 * === BOUCLE TANT QUE
	 * ========================= */

	@Test
	void while_basic_loop() {
		new LeaAsserts("""
				i <- 1;
				tant que i < 4 faire
				    écrire(i);
				    i <- i + 1;
				fin tant que
				""")
		.assertHasNoError()
		.assertOutputs(new Int(1), new Int(2), new Int(3));
	}

	@Test
	void while_zero_iterations() {
		new LeaAsserts("""
				tant que faux faire
				    écrire(1);
				fin tant que
				""")
		.assertHasNoError()
		.assertOutputs();
	}

	/* =========================
	 * === BOUCLE POUR
	 * ========================= */

	@Test
	void for_basic_increment() {
		new LeaAsserts("""
				pour i de 1 à 3 faire
				    écrire(i);
				fin pour
				""")
		.assertHasNoError()
		.assertOutputs(new Int(1), new Int(2), new Int(3));
	}

	@Test
	void for_basic_decrement() {
		new LeaAsserts("""
				pour i de 3 à 1 faire
				    écrire(i);
				fin pour
				""")
		.assertHasNoError()
		.assertOutputs(new Int(3), new Int(2), new Int(1));
	}

	@Test
	void for_explicit_step() {
		new LeaAsserts("""
				pour i de 0 à 10 pas 4 faire
				    écrire(i);
				fin pour
				""")
		.assertHasNoError()
		.assertOutputs(new Int(0), new Int(4), new Int(8));
	}

	@Test
	void for_single_evaluation_of_bounds() {
		new LeaAsserts("""
				n <- 3;
				pour i de 1 à n faire
				    n <- 100;
				    écrire(i);
				fin pour
				""")
		.assertHasNoError()
		.assertOutputs(new Int(1), new Int(2), new Int(3));
	}

	/* =========================
	 * === IMBRICATIONS ET ERREURS
	 * ========================= */

	@Test
	void nested_loops() {
		new LeaAsserts("""
				pour i de 1 à 2 faire
				    pour j de 1 à i faire
				        écrire(j);
				    fin pour
				fin pour
				""")
		.assertHasNoError()
		.assertOutputs(new Int(1), new Int(1), new Int(2));
	}

	@Test
	void error_while_condition_type() {
		new LeaAsserts("tant que 1 faire écrire(1); fin tant que")
		.assertHasErrorContaining(Phase.RUNTIME, "Type");
	}
}