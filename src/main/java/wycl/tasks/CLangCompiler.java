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
package wycl.tasks;

import java.util.Collections;
import java.util.List;

import wycc.util.AbstractCompilationUnit.Value;
import wycc.util.Pair;
import wycl.core.CLangFile;
import wycl.core.CLangFile.Expression;
import wycl.core.CLangFile.Statement;
import wycl.core.CLangFile.Declaration;
import wyil.util.AbstractTranslator;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Decl.*;
import wyil.lang.WyilFile.Expr.*;
import wyil.lang.WyilFile.Stmt.*;
import wyil.util.IncrementalSubtypingEnvironment;
import wyil.util.Subtyping;
import wyil.util.TypeMangler;

public class CLangCompiler extends AbstractTranslator<Declaration,Statement,Expression> {
	/**
	 * Provides a standard mechanism for writing out type mangles.
	 */
	private final static TypeMangler mangler = new TypeMangler.Default();

	/**
	 * Provides a standard mechanism for checking whether two Whiley types are
	 * subtypes or not.
	 */
	private final static Subtyping.Environment subtyping = new IncrementalSubtypingEnvironment();

	/**
	 * Represents the JavaScriptFile which is being written to.
	 */
	private final CLangFile cFile;

	public CLangCompiler(CLangFile cFile) {
		super(subtyping);
		this.cFile = cFile;
	}

	public void visitModule(WyilFile wf) {
		// Translate local units
		for (Decl.Unit unit : wf.getModule().getUnits()) {
			for (Decl decl : unit.getDeclarations()) {
				CLangFile.Declaration d = (CLangFile.Declaration) visitDeclaration(decl);
				if (d != null) {
					cFile.getDeclarations().add(d);
				}
			}
		}
	}

	// ====================================================================================
	// Declarations
	// ====================================================================================

	@Override
	public Declaration constructImport(Import d) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructType(Type d, List<Expression> invariant) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructStaticVariable(StaticVariable d, Expression initialiser) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructProperty(Property decl, Statement body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructVariant(Variant decl, List<Expression> clauses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Declaration constructFunction(Function d, List<Expression> precondition, List<Expression> postcondition, Statement body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructMethod(Method d, List<Expression> precondition, List<Expression> postcondition, Statement body) {
		return new Declaration.Method(d.getName().get(), Collections.EMPTY_LIST, (Statement.Block) body);
	}

	@Override
	public Expression constructLambda(Lambda decl, Expression body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructAssert(Assert stmt, Expression condition) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructAssign(Assign stmt, List<Expression> lvals, List<Expression> rvals) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructAssume(Assume stmt, Expression condition) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructBlock(Block stmt, List<Statement> stmts) {
		return new Statement.Block(stmts);
	}

	@Override
	public Statement constructBreak(Break stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructContinue(Continue stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructDebug(Debug stmt, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructDoWhile(DoWhile stmt, Statement body, Expression condition, List<Expression> invariant) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructFail(Fail stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructFor(For stmt, Pair<Expression, Expression> range, List<Expression> invariant, Statement body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructIfElse(IfElse stmt, Expression condition, Statement trueBranch, Statement falseBranch) {
		return new Statement.If(condition, trueBranch, falseBranch);
	}

	@Override
	public Statement constructInitialiser(Initialiser stmt, Expression initialiser) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructInvokeStmt(Invoke expr, List<Expression> arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement constructIndirectInvokeStmt(IndirectInvoke expr, Expression source, List<Expression> arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Statement constructNamedBlock(NamedBlock stmt, List<Statement> stmts) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructReturn(Return stmt, Expression ret) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructSkip(Skip stmt) {
		return new Statement.Skip();
	}

	@Override
	public Statement constructSwitch(Switch stmt, Expression condition, List<Pair<List<Expression>, Statement>> cases) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructWhile(While stmt, Expression condition, List<Expression> invariant, Statement body) {
		return new Statement.While(condition, body);
	}

	@Override
	public Expression constructArrayAccessLVal(ArrayAccess expr, Expression source, Expression index) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructDereferenceLVal(Dereference expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructFieldDereferenceLVal(FieldDereference expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructRecordAccessLVal(RecordAccess expr, Expression source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructRecordUpdate(RecordUpdate expr, Expression source, Expression value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructTupleInitialiserLVal(TupleInitialiser expr, List<Expression> source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructVariableAccessLVal(VariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructStaticVariableAccessLVal(StaticVariableAccess expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructArrayAccess(ArrayAccess expr, Expression source, Expression index) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructArrayLength(ArrayLength expr, Expression source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructArrayGenerator(ArrayGenerator expr, Expression value, Expression length) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructArrayInitialiser(ArrayInitialiser expr, List<Expression> values) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructArrayUpdate(ArrayUpdate expr, Expression source, Expression index, Expression value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructBitwiseComplement(BitwiseComplement expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseAnd(BitwiseAnd expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseOr(BitwiseOr expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseXor(BitwiseXor expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseShiftLeft(BitwiseShiftLeft expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseShiftRight(BitwiseShiftRight expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructCast(Cast expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructConstant(Constant expr) {
		Value v = expr.getValue();
		//
		if(v instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) v;
			if(b.get()) {
				return new Expression.IntConstant(1);
			} else {
				return new Expression.IntConstant(0);
			}
		} else if(v instanceof Value.Int) {
			Value.Int b = (Value.Int) v;
			// TODO: clearly a hack for now.
			return new Expression.IntConstant(b.get().intValueExact());
		} else {
			// TODO Auto-generated method stub
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Expression constructDereference(Dereference expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructFieldDereference(FieldDereference expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructEqual(Equal expr, Expression lhs, Expression rhs) {
		return new Expression.Equals(lhs, rhs);
	}

	@Override
	public Expression constructIntegerLessThan(IntegerLessThan expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerLessThanOrEqual(IntegerLessThanOrEqual expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerGreaterThan(IntegerGreaterThan expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerGreaterThanOrEqual(IntegerGreaterThanOrEqual expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerNegation(IntegerNegation expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerAddition(IntegerAddition expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerSubtraction(IntegerSubtraction expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerMultiplication(IntegerMultiplication expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerDivision(IntegerDivision expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerRemainder(IntegerRemainder expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIntegerExponent(IntegerExponent expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructIs(Is expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLogicalAnd(LogicalAnd expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLogicalImplication(LogicalImplication expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLogicalIff(LogicalIff expr, Expression lhs, Expression rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLogicalNot(LogicalNot expr, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLogicalOr(LogicalOr expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructExistentialQuantifier(ExistentialQuantifier expr, List<Pair<Expression, Expression>> ranges, Expression body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructUniversalQuantifier(UniversalQuantifier expr, List<Pair<Expression, Expression>> ranges, Expression body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructInvoke(Invoke expr, List<Expression> arguments) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructIndirectInvoke(IndirectInvoke expr, Expression source, List<Expression> arguments) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructLambdaAccess(LambdaAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructNew(New expr, Expression operand) {
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructNotEqual(NotEqual expr, Expression lhs, Expression rhs) {
		return new Expression.Equals(true, lhs, rhs);
	}

	@Override
	public Expression constructOld(Old expr, Expression operand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructRecordAccess(RecordAccess expr, Expression source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructRecordInitialiser(RecordInitialiser expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructTupleInitialiser(TupleInitialiser expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructStaticVariableAccess(StaticVariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructVariableAccess(VariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement applyImplicitCoercion(wyil.lang.WyilFile.Type target, wyil.lang.WyilFile.Type source,
			Statement expr) {
		// TODO Auto-generated method stub
		return null;
	}

}
