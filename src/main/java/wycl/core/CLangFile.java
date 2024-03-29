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

import wycc.util.Pair;
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

		public static class Include implements Declaration {
			private final String include;

			public Include(String include) {
				this.include = include;
			}

			public String getInclude() {
				return include;
			}
		}

		public static class TypeDef extends Abstract implements Declaration {
			private final Type type;

			public TypeDef(String name, Type type) {
				super(name);
				this.type = type;
			}

			public Type getType() {
				return type;
			}
		}

		public static class Method extends Abstract implements Declaration {
			private final Type returnType;
			private final List<Parameter> parameters;
			private final Statement.Block body;

			public Method(Type returnType, String name, List<Parameter> parameters, Statement.Block body) {
				super(name);
				this.returnType = returnType;
				this.parameters = parameters;
				this.body = body;
			}

			public Type getReturnType() {
				return returnType;
			}

			public List<Parameter> getParameters() {
				return parameters;
			}

			public Statement.Block getBody() {
				return body;
			}
		}

		public static class Variable extends Abstract implements Statement {
			private final Type type;
			private final Expression initialiser;

			public Variable(Type type, String name, Expression initialiser) {
				super(name);
				this.type = type;
				this.initialiser = initialiser;
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
			public Parameter(Type type, String name) {
				super(type, name, null);
			}
		}
	}

	// =========================================================================
	// Statements
	// =========================================================================

	public interface Statement extends Term {

		public static class Assign implements Statement {
			private final Expression lhs;
			private final Expression rhs;

			private Assign(Expression lhs, Expression rhs) {
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

		public static class Continue implements Statement {
			private Continue() {
			}
		}

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

		public static class Break implements Statement {
			private Break() {
			}
		}

		public static class DoWhile implements Statement {
			private final Expression condition;
			private final Statement body;

			private DoWhile(Expression condition, Statement body) {
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

		public static class For implements Statement {
			private final Statement initialiser;
			private final Expression condition;
			private final Statement increment;
			private final Statement body;

			private For(Statement initialiser, Expression condition, Statement increment, Statement body) {
				this.initialiser = initialiser;
				this.condition = condition;
				this.increment = increment;
				this.body = body;
			}

			public Statement getInitialiser() {
				return initialiser;
			}

			public Expression getCondition() {
				return condition;
			}

			public Statement getIncrement() {
				return increment;
			}

			public Statement getBody() {
				return body;
			}
		}

		public static class Skip implements Statement {

		}

		public static class If implements Statement {
			private final Expression condition;
			private final Statement trueBranch;
			private final Statement falseBranch;

			private If(Expression condition, Statement trueBranch, Statement falseBranch) {
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

			private While(Expression condition, Statement body) {
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

		public static class Return implements Statement {
			private final Expression operand;

			private Return(Expression operand) {
				this.operand = operand;
			}

			public Expression getOperand() {
				return operand;
			}
		}
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	public interface Expression extends Statement {

		/**
		 * Determine whether or not this expression requires parenthesis when used as
		 * part of an another expression.
		 *
		 * @return
		 */
		public boolean requiresParenthesis();

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

			public abstract String getOperatorString();

			@Override
			public boolean requiresParenthesis() {
				return true;
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

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public abstract class Postfix implements Expression {
			private final Expression operand;

			private Postfix(Expression operand) {
				this.operand = operand;
			}

			public Expression getOperand() {
				return operand;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		// ======================================================
		// Arithmetic
		// ======================================================

		public class Add extends Infix {
			private Add(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "+";
			}
		}

		public class Sub extends Infix {
			private Sub(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "-";
			}
		}

		public class Div extends Infix {
			private Div(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "/";
			}
		}

		public class Mul extends Infix {
			private Mul(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "*";
			}
		}

		public class Rem extends Infix {
			private Rem(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "%";
			}
		}

		public class Neg extends Prefix {
			private Neg(Expression operand) {
				super(operand);
			}
		}

		public class BoolConstant implements Expression {
			private final boolean constant;

			private BoolConstant(boolean constant) {
				this.constant = constant;
			}

			public boolean getConstant() {
				return constant;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public class IntConstant implements Expression {
			private final boolean hex;
			private final int constant;

			private IntConstant(boolean hex, int constant) {
				this.hex = hex;
				this.constant = constant;
			}

			public boolean inHex() {
				return hex;
			}

			public int getConstant() {
				return constant;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public class Invoke implements Expression {
			private final String name;
			private final List<Expression> arguments;

			private Invoke(String name, List<Expression> arguments) {
				this.name = name;
				this.arguments = arguments;
			}

			public String getName() {
				return name;
			}

			public List<Expression> getArguments() {
				return arguments;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		// ======================================================
		// Logical
		// ======================================================

		public class Equals extends Infix {
			private Equals(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "==";
			}
		}

		public class NotEquals extends Infix {
			private NotEquals(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "!=";
			}
		}

		public class LessThan extends Infix {
			private LessThan(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "<";
			}
		}

		public class LessThanEqual extends Infix {
			private LessThanEqual(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "<=";
			}
		}

		public class GreaterThan extends Infix {
			private GreaterThan(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return ">";
			}
		}

		public class GreaterThanEqual extends Infix {
			private GreaterThanEqual(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return ">=";
			}
		}

		public class Not extends Prefix {
			private Not(Expression operand) {
				super(operand);
			}
		}

		public class And extends Infix {
			private And(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "&&";
			}
		}

		public class Or extends Infix {
			private Or(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "||";
			}
		}

		// ======================================================
		// Bitwise
		// ======================================================

		public class BitwiseAnd extends Infix {
			private BitwiseAnd(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "&";
			}
		}

		public class BitwiseOr extends Infix {
			private BitwiseOr(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "|";
			}
		}

		public class ShiftLeft extends Infix {
			private ShiftLeft(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return "<<";
			}
		}

		public class ShiftRight extends Infix {
			private ShiftRight(Expression lhs, Expression rhs) {
				super(lhs, rhs);
			}

			@Override
			public String getOperatorString() {
				return ">>";
			}
		}

		// ======================================================
		// Other
		// ======================================================

		public class ArrayAccess implements Expression {
			private final Expression source;
			private final Expression index;

			public ArrayAccess(Expression source, Expression index) {
				this.source = source;
				this.index = index;
			}

			public Expression getSource() {
				return source;
			}

			public Expression getIndex() {
				return index;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public class Dereference extends Prefix {
			public Dereference(Expression source) {
				super(source);
			}
		}

		public class FieldDereference extends Prefix {
			private final String field;
			public FieldDereference(Expression source, String field) {
				super(source);
				this.field = field;
			}
			public String getField() {
				return field;
			}
		}

		public class Var implements Expression {
			private final String name;

			private Var(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public class DesignatedInitialiser implements Expression {
			private final List<Pair<String, Expression>> fields;

			public DesignatedInitialiser(List<Pair<String, Expression>> fields) {
				this.fields = fields;
			}

			public List<Pair<String, Expression>> getFields() {
				return fields;
			}

			@Override
			public boolean requiresParenthesis() {
				return false;
			}
		}

		public class FieldAccess extends Postfix {
			private final String field;

			public FieldAccess(Expression operand, String field) {
				super(operand);
				this.field = field;
			}

			public String getField() {
				return field;
			}
		}
	}

	// =========================================================================
	// Types
	// =========================================================================

	public interface Type {

		public class Bool implements Type {
			private Bool() {

			}
		}

		public class Int implements Type {
			private boolean signed;
			private int width;

			private Int(boolean signed) {
				this.signed = signed;
				this.width = Integer.MAX_VALUE;
			}

			private Int(boolean signed, int width) {
				if (width != 8 && width != 16 && width != 32 && width != 64) {
					throw new IllegalArgumentException("invalid integer width");
				}
				this.signed = signed;
				this.width = width;
			}

			public boolean hasFixedWidth() {
				return width != Integer.MAX_VALUE;
			}

			public boolean isSigned() {
				return signed;
			}

			public int getWidth() {
				return width;
			}
		}

		public class Nominal implements Type {
			private final String name;

			private Nominal(String name) {
				this.name = name;
			}

			public String getName() {
				return name;
			}
		}

		public class Pointer implements Type {
			private final Type element;

			public Pointer(Type element) {
				this.element = element;
			}

			public Type getElement() {
				return element;
			}
		}

		public class Struct implements Type {
			private final List<Pair<Type, String>> fields;

			public Struct(List<Pair<Type, String>> fields) {
				this.fields = fields;
			}

			public List<Pair<Type, String>> getFields() {
				return fields;
			}
		}

		public class Void implements Type {
			private Void() {

			}
		}
	}

	// =========================================================================
	// Declaration Constructors
	// =========================================================================

	public static Declaration TYPEDEF(String name, Type type) {
		return new Declaration.TypeDef(name, type);
	}

	// =========================================================================
	// Statement Constructors
	// =========================================================================

	public static Statement ASSIGN(Expression lhs, Expression rhs) {
		return new Statement.Assign(lhs, rhs);
	}

	public static Statement CONTINUE() {
		return new Statement.Continue();
	}

	public static Statement BREAK() {
		return new Statement.Break();
	}

	public static Statement FOR(Statement initialiser, Expression cond, Statement increment, Statement body) {
		return new Statement.For(initialiser, cond, increment, body);
	}

	public static Statement IF(Expression cond, Statement trueBranch, Statement falseBranch) {
		return new Statement.If(cond, trueBranch, falseBranch);
	}

	public static Statement WHILE(Expression cond, Statement body) {
		return new Statement.While(cond, body);
	}

	public static Statement DOWHILE(Expression cond, Statement body) {
		return new Statement.DoWhile(cond, body);
	}

	public static Statement RETURN(Expression operand) {
		return new Statement.Return(operand);
	}

	// =========================================================================
	// Expression Constructors
	// =========================================================================

	public static Expression ARRAY_ACCESS(Expression source, Expression index) {
		return new Expression.ArrayAccess(source, index);
	}

	public static Expression BOOL_CONST(boolean value) {
		return new Expression.BoolConstant(value);
	}

	public static Expression BIT_AND(Expression lhs, Expression rhs) {
		return new Expression.BitwiseAnd(lhs, rhs);
	}

	public static Expression BIT_OR(Expression lhs, Expression rhs) {
		return new Expression.BitwiseOr(lhs, rhs);
	}

	public static Expression SHL(Expression lhs, Expression rhs) {
		return new Expression.ShiftLeft(lhs, rhs);
	}

	public static Expression SHR(Expression lhs, Expression rhs) {
		return new Expression.ShiftRight(lhs, rhs);
	}

	public static Expression INT_CONST(int value) {
		return new Expression.IntConstant(false,value);
	}

	public static Expression HEX_CONST(int value) {
		return new Expression.IntConstant(true,value);
	}

	public static Expression DEREFERENCE(Expression source) {
		return new Expression.Dereference(source);
	}

	public static Expression EQ(Expression lhs, Expression rhs) {
		return new Expression.Equals(lhs, rhs);
	}

	public static Expression FIELD_ACCESS(Expression source, String field) {
		return new Expression.FieldAccess(source, field);
	}


	public static Expression FIELD_DEREFERENCE(Expression source, String field) {
		return new Expression.FieldDereference(source, field);
	}

	public static Expression INVOKE(String name, List<Expression> arguments) {
		return new Expression.Invoke(name, arguments);
	}

	public static Expression INITIALISER(List<Pair<String, Expression>> fields) {
		return new Expression.DesignatedInitialiser(fields);
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

	// =========================================================================
	// Type Constructors
	// =========================================================================

	public static Type BOOL() {
		return new Type.Bool();
	}

	public static Type INT() {
		return new Type.Int(true);
	}

	public static Type INT(int width) {
		if(width != 8 && width != 16 && width != 32 && width != 64) {
			throw new IllegalArgumentException("invalid integer width");
		}
		return new Type.Int(true,width);
	}

	public static Type NOMINAL(String name) {
		return new Type.Nominal(name);
	}

	public static Type POINTER(Type element) {
		return new Type.Pointer(element);
	}

	public static Type STRUCT(List<Pair<Type, String>> fields) {
		return new Type.Struct(fields);
	}

	public static Type UINT(int width) {
		if(width != 8 && width != 16 && width != 32 && width != 64) {
			throw new IllegalArgumentException("invalid integer width");
		}
		return new Type.Int(false,width);
	}

	public static Type VOID() {
		return new Type.Void();
	}
}
