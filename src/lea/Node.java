package lea;

import java.util.*;

public sealed interface Node {

	public sealed interface Instruction 									extends 	Node {}
	public sealed interface Expression										extends 	Node {}
	
	public record Sequence(List<Instruction> commands)						implements	Instruction {}
	public record Assignment(Identifier id, Expression value)				implements	Instruction {}
	public record Write(Expression argument)								implements	Instruction {}
	public record If(Expression cond, Instruction bodyT, Instruction bodyF)	implements	Instruction {}
	public record While(Expression cond, Instruction body)					implements  Instruction {}
	public record For(Identifier i, Expression d, Expression f, Optional<Expression> step, Instruction body)					implements  Instruction {}
	
	public record Identifier(String name)									implements	Expression {}
	public record Sum(Expression left, Expression right)					implements	Expression {}
	public record Difference(Expression left, Expression right)				implements	Expression {}
	public record Product(Expression left, Expression right)				implements	Expression {}
	public record Inverse(Expression argument)								implements	Expression {}
	public record And(Expression left, Expression right)					implements	Expression {}
	public record Or(Expression left, Expression right)						implements	Expression {}
	public record Equal(Expression left, Expression right)					implements	Expression {}
	public record Lower(Expression left, Expression right)					implements	Expression {}

	public sealed interface Value											extends		Expression {}
	public record Bool(boolean value)										implements	Value {}
	public record Int(int value)											implements	Value {}
	
	public record ErrorNode()												implements	Instruction, Expression{}

}
