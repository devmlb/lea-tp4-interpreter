package lea;

import java.util.*;

import java_cup.parse_action;
import lea.Node.*;
import lea.Reporter.Phase;

public class Interpreter {

	private final Map<Identifier, Value> variables = new HashMap<>();
	private final Reporter reporter;

	public Interpreter(Reporter reporter) {
		this.reporter=reporter;
	}

	public void execute(Instruction instruction) {
		try {
			interpret(instruction);
		} catch (PanicException e) {}
	}

	/**
	 * 
	 * @param instruction
	 * @throws PanicException
	 */
	private void interpret(Instruction instruction) throws PanicException {
		switch(instruction) {
		case Sequence s		-> interpret(s);
		case Assignment a	-> variables.put(a.id(), eval(a.value()));
		case Write w		-> interpret(w);
		case If i			-> interpret(i);
		case While wh		-> interpret(wh);
		case For f			-> interpret(f);
		case ErrorNode e	-> throw error(e, "Le programme contient une erreur de syntaxe");
		}
	}

	private void interpret(Write w) throws PanicException {
		Value value = eval(w.argument());
		reporter.out(value);
		switch(value) {
		case Int i	-> System.out.println(i.value());
		case Bool b	-> System.out.println(b.value());
		};
	}

	private void interpret(Sequence sequence) throws PanicException {
		for(var commande : sequence.commands()) 
			interpret(commande);
	}

	private void interpret(If i) throws PanicException {
		if(evalAsBool(i.cond())) {
			interpret(i.bodyT());
		} else {
			interpret(i.bodyF());
		}
	}

	private void interpret(While wh) throws PanicException {
		while (evalAsBool(wh.cond())) {
			interpret(wh.body());
		}
	}

	private void interpret(For f) throws PanicException {
		variables.put(f.i(), eval(f.d()));

		int s = 1;
		if (evalAsInt(f.d()) > evalAsInt(f.f())) s = -1;
		if (f.step().isPresent()) s = evalAsInt(f.step().get());


		if (s == 0) return;
		
		int endValue = evalAsInt(f.f());
		
		for (int current = evalAsInt(variables.get(f.i())); s > 0 ? current <= endValue : current >= endValue; current += s) {
			variables.put(f.i(), new Int(current));
			interpret(f.body());
		}
	}
	
	/**
	 * 
	 * @param expression
	 * @return
	 * @throws PanicException
	 */
	private Value eval(Expression expression) throws PanicException {
		return switch(expression) {
		case Value l		-> l;
		case Identifier id 	-> eval(id);
		case Sum s			-> new Int(evalAsInt(s.left()) + evalAsInt(s.right()));
		case Difference d	-> new Int(evalAsInt(d.left()) - evalAsInt(d.right()));
		case Product p		-> new Int(evalAsInt(p.left()) * evalAsInt(p.right()));
		case Lower l		-> new Bool(evalAsInt(l.left()) < evalAsInt(l.right()));
		case Equal e 		-> new Bool(eval(e.left()).equals(eval(e.right())));
		case And a			-> new Bool(evalAsBool(a.left()) && evalAsBool(a.right()));
		case Or o 			-> new Bool(evalAsBool(o.left()) || evalAsBool(o.right()));
		case Inverse i		-> new Int(-evalAsInt(i.argument()));
		case ErrorNode e	-> throw error(e, "Le programme contient une erreur de syntaxe");
		};
	}
	
	private Value eval(Identifier id) throws PanicException {
		Value v = variables.get(id);
		if (v != null) return v;
		throw error(id, "Utilisation d'une variable pas initialisée");
	}

	private boolean evalAsBool(Expression expression) throws PanicException {
		return switch(eval(expression)) {
		case Bool b -> b.value();
		default -> throw error(expression, "Type (booléen)");
		};
	}

	private int evalAsInt(Expression expression) throws PanicException {
		return switch(eval(expression)) {
		case Int i -> i.value();
		default -> throw error(expression, "Type (entier)");
		};
	}	
	
	/* ***************
	 * 	Gestion des erreurs d'exécution
	 ******************/
	private PanicException error(Node n, String message) {
		reporter.error(Phase.RUNTIME, n, message);
		return new PanicException(message);
	}
	
	private static class PanicException extends Exception {
		private static final long serialVersionUID = 1L;
		public PanicException(String message) {super(message);}
	}


	
	
}
