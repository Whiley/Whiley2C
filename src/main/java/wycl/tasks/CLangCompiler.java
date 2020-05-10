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

import java.util.List;

import wybs.lang.Build.Meter;
import wycc.util.Pair;
import wycl.core.CLangFile;
import wycl.core.CLangFile.Term;
import wycl.util.AbstractTranslator;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Decl.*;
import wyil.lang.WyilFile.Expr.*;
import wyil.lang.WyilFile.Stmt.*;
import wyil.util.IncrementalSubtypingEnvironment;
import wyil.util.Subtyping;
import wyil.util.TypeMangler;

public class CLangCompiler extends AbstractTranslator<Term> {
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

	public CLangCompiler(Meter meter, CLangFile cFile) {
		super(meter, subtyping);
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
	public Term constructImport(Import d) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructType(Type d, List<Term> invariant) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructStaticVariable(StaticVariable d, Term initialiser) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructProperty(Property decl, List<Term> clauses) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructFunction(Function d, List<Term> precondition, List<Term> postcondition, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructMethod(Method d, List<Term> precondition, List<Term> postcondition, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLambda(Lambda decl, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructAssert(Assert stmt, Term condition) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructAssign(Assign stmt, List<Term> lvals, List<Term> rvals) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructAssume(Assume stmt, Term condition) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBlock(Block stmt, List<Term> stmts) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBreak(Break stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructContinue(Continue stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructDebug(Debug stmt, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructDoWhile(DoWhile stmt, Term body, Term condition, List<Term> invariant) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructFail(Fail stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructFor(For stmt, Pair<Term, Term> range, List<Term> invariant, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIfElse(IfElse stmt, Term condition, Term trueBranch, Term falseBranch) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructInitialiser(Initialiser stmt, Term initialiser) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructNamedBlock(NamedBlock stmt, List<Term> stmts) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructReturn(Return stmt, Term ret) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructSkip(Skip stmt) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructSwitch(Switch stmt, Term condition, List<Pair<List<Term>, Term>> cases) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructWhile(While stmt, Term condition, List<Term> invariant, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructArrayAccessLVal(ArrayAccess expr, Term source, Term index) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructDereferenceLVal(Dereference expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructFieldDereferenceLVal(FieldDereference expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructRecordAccessLVal(RecordAccess expr, Term source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructTupleInitialiserLVal(TupleInitialiser expr, List<Term> source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructVariableAccessLVal(VariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructArrayAccess(ArrayAccess expr, Term source, Term index) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructArrayLength(ArrayLength expr, Term source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructArrayGenerator(ArrayGenerator expr, Term value, Term length) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructArrayInitialiser(ArrayInitialiser expr, List<Term> values) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseComplement(BitwiseComplement expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseAnd(BitwiseAnd expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseOr(BitwiseOr expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseXor(BitwiseXor expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseShiftLeft(BitwiseShiftLeft expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructBitwiseShiftRight(BitwiseShiftRight expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructCast(Cast expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructConstant(Constant expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructDereference(Dereference expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructFieldDereference(FieldDereference expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructEqual(Equal expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerLessThan(IntegerLessThan expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerLessThanOrEqual(IntegerLessThanOrEqual expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerGreaterThan(IntegerGreaterThan expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerGreaterThanOrEqual(IntegerGreaterThanOrEqual expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerNegation(IntegerNegation expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerAddition(IntegerAddition expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerSubtraction(IntegerSubtraction expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerMultiplication(IntegerMultiplication expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerDivision(IntegerDivision expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIntegerRemainder(IntegerRemainder expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIs(Is expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLogicalAnd(LogicalAnd expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLogicalImplication(LogicalImplication expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLogicalIff(LogicalIff expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLogicalNot(LogicalNot expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLogicalOr(LogicalOr expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructExistentialQuantifier(ExistentialQuantifier expr, List<Pair<Term, Term>> ranges, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructUniversalQuantifier(UniversalQuantifier expr, List<Pair<Term, Term>> ranges, Term body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructInvoke(Invoke expr, List<Term> arguments) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructIndirectInvoke(IndirectInvoke expr, Term source, List<Term> arguments) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructLambdaAccess(LambdaAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructNew(New expr, Term operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructNotEqual(NotEqual expr, Term lhs, Term rhs) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructRecordAccess(RecordAccess expr, Term source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructRecordInitialiser(RecordInitialiser expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructTupleInitialiser(TupleInitialiser expr, List<Term> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructStaticVariableAccess(StaticVariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term constructVariableAccess(VariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Term applyImplicitCoercion(wyil.lang.WyilFile.Type target, wyil.lang.WyilFile.Type source, Term expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

}
