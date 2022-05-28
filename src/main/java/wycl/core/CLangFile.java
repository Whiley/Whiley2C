// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycl.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wycl.core.CLangFile;

public class CLangFile {

	// =========================================================================
	//
	// =========================================================================

	/**
	 * The list of top-level declarations within this file.
	 */
	private List<Declaration> declarations;

	public CLangFile() {
		this.declarations = new ArrayList<>();
	}

	public List<Declaration> getDeclarations() {
		return declarations;
	}

	// =========================================================================
	// Term
	// =========================================================================
	public interface Term {

	}

	// =========================================================================
	// Declarations
	// =========================================================================

	/**
	 * A declaration (e.g. class or method) within a Java file
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Declaration extends Term {

		public static class Abstract implements Declaration {
			/**
			 * The name of the class in question
			 */
			private final String name;

			public Abstract(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}
		}

		public static class Method extends Abstract implements Declaration {
			private final List<Parameter> parameters;
			private final Statement.Block body;

			public Method(String name, List<Parameter> parameters, Statement.Block body) {
				super(name);
				this.parameters = parameters;
				this.body = body;
			}

			public List<Parameter> getParameters() {
				return parameters;
			}

			public Statement.Block getBody() {
				return body;
			}
		}

		public static class Variable implements Declaration, Statement {
			private final Type type;
			private final String name;
			private final Expression initialiser;

			public Variable(Type type, String name, Expression initialiser) {
				this.type = type;
				this.name = name;
				this.initialiser = initialiser;
			}

			public String getName(int ith) {
				return name;
			}

			public Type getType() {
				return type;
			}

			public boolean hasInitialiser(int ith) {
				return initialiser != null;
			}

			public Expression getInitialiser() {
				return initialiser;
			}
		}


		public static class Parameter extends Variable {

			public Parameter(Type type, String name, Expression initialiser) {
				super(type, name, initialiser);
			}

		}


	}

	// =========================================================================
	// Statements
	// =========================================================================

	public interface Statement extends Term {

		public static class Block implements Statement {
			private List<Statement> terms;

			public Block() {
				terms = new ArrayList<>();
			}

			public Block(List<Statement> terms) {
				this.terms = new ArrayList<>(terms);
			}

			public Block(Statement... terms) {
				this.terms = Arrays.asList(terms);
			}

			public List<Statement> getTerms() {
				return terms;
			}
		}

		public static class Skip implements Statement {

		}

		public static class If implements Statement {
			private final Expression condition;
			private final Statement trueBranch;
			private final Statement falseBranch;

			public If(Expression condition, Statement trueBranch, Statement falseBranch) {
				this.condition = condition;
				this.trueBranch = trueBranch;
				this.falseBranch = falseBranch;
			}

			public Expression getCondition() {
				return condition;
			}

			public Statement getTrueBranch() {
				return trueBranch;
			}

			public Statement getFalseBranch() {
				return falseBranch;
			}
		}

		public static class While implements Statement {
			private final Expression condition;
			private final Statement body;

			public While(Expression condition, Statement body) {
				this.condition = condition;
				this.body = body;
			}

			public Expression getCondition() {
				return condition;
			}

			public Statement getBody() {
				return body;
			}
		}
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	public interface Expression extends Term {

		public abstract class Infix implements Expression {
			private final Expression lhs;
			private final Expression rhs;

			private Infix(Expression lhs, Expression rhs) {
				this.lhs = lhs;
				this.rhs = rhs;
			}

			public Expression getLeftHandSide() {
				return lhs;
			}

			public Expression getRightHandSide() {
				return rhs;
			}
		}

		public abstract class Prefix implements Expression {
			private final Expression operand;

			private Prefix(Expression operand) {
				this.operand = operand;
			}

			public Expression getOperand() {
				return operand;
			}
		}

		// ======================================================
		// Arithmetic
		// ======================================================

		public class Add extends Infix{
			private Add(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Sub extends Infix{
			private Sub(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Div extends Infix{
			private Div(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Mul extends Infix{
			private Mul(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Rem extends Infix{
			private Rem(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Neg extends Prefix {
			private Neg(Expression operand) {
				super(operand);
			}
		}
		public class IntConstant implements Expression {
			private final int constant;

			public IntConstant(int constant) {
				this.constant = constant;
			}

			public int getConstant() {
				return constant;
			}
		}

		// ======================================================
		// Logical
		// ======================================================

		public class Equals extends Infix {
			private Equals(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class NotEquals extends Infix {
			private NotEquals(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class LessThan extends Infix {
			private LessThan(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class LessThanEqual extends Infix {
			private LessThanEqual(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class GreaterThan extends Infix {
			private GreaterThan(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class GreaterThanEqual extends Infix {
			private GreaterThanEqual(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Not extends Prefix {
			private Not(Expression operand) {
				super(operand);
			}
		}

		public class And extends Infix {
			private And(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		public class Or extends Infix {
			private Or(Expression lhs, Expression rhs) {
				super(lhs,rhs);
			}
		}

		// ======================================================
		// Other
		// ======================================================

		public class Var implements Expression{
			private final String name;
			private Var(String name) {
				this.name = name;
			}
			public String getName() {
				return name;
			}
		}
	}

	// =========================================================================
	// Types
	// =========================================================================

	public interface Type {

	}

	// =========================================================================
	// Constructors
	// =========================================================================

	public static Expression EQ(Expression lhs, Expression rhs) {
		return new Expression.Equals(lhs, rhs);
	}

	public static Expression NEQ(Expression lhs, Expression rhs) {
		return new Expression.NotEquals(lhs, rhs);
	}

	public static Expression LT(Expression lhs, Expression rhs) {
		return new Expression.LessThan(lhs, rhs);
	}

	public static Expression LTEQ(Expression lhs, Expression rhs) {
		return new Expression.LessThanEqual(lhs, rhs);
	}

	public static Expression GT(Expression lhs, Expression rhs) {
		return new Expression.GreaterThan(lhs, rhs);
	}

	public static Expression GTEQ(Expression lhs, Expression rhs) {
		return new Expression.GreaterThanEqual(lhs, rhs);
	}

	public static Expression AND(Expression lhs, Expression rhs) {
		return new Expression.And(lhs, rhs);
	}

	public static Expression OR(Expression lhs, Expression rhs) {
		return new Expression.Or(lhs, rhs);
	}

	public static Expression NOT(Expression operand) {
		return new Expression.Not(operand);
	}

	public static Expression ADD(Expression lhs, Expression rhs) {
		return new Expression.Add(lhs, rhs);
	}

	public static Expression SUB(Expression lhs, Expression rhs) {
		return new Expression.Sub(lhs, rhs);
	}

	public static Expression MUL(Expression lhs, Expression rhs) {
		return new Expression.Mul(lhs, rhs);
	}

	public static Expression DIV(Expression lhs, Expression rhs) {
		return new Expression.Div(lhs, rhs);
	}

	public static Expression REM(Expression lhs, Expression rhs) {
		return new Expression.Rem(lhs, rhs);
	}

	public static Expression NEG(Expression operand) {
		return new Expression.Neg(operand);
	}

	public static Expression VAR(String name) {
		return new Expression.Var(name);
	}
}

