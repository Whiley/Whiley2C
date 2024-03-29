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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import wycc.util.AbstractCompilationUnit.Tuple;
import wycc.util.AbstractCompilationUnit.Value;
import wycc.util.Pair;
import wycc.util.Trie;

import static wycl.core.CLangFile.*;
import wycl.core.CLangFile;
import wycl.core.CLangFile.Expression;
import wycl.core.CLangFile.Statement;
import wycl.core.CLangFile.Declaration;
import wycl.core.CLangFile.Type;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Decl.*;
import wyil.lang.WyilFile.Expr.*;
import wyil.lang.WyilFile.Stmt.*;
import wyil.util.IncrementalSubtypingEnvironment;
import wyil.util.Subtyping;
import wyil.util.TypeMangler;
import wycl.util.AbstractTranslator;

public class CLangCompiler extends AbstractTranslator<Declaration,Statement,Expression,Type> {
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
     * Flag to signal whether or not to apply mangling.  By default this is enabled.
     */
    private boolean mangling = true;

	/**
	 * Represents the JavaScriptFile which is being written to.
	 */
	private final CLangFile cFile;

	public CLangCompiler(CLangFile cFile) {
		super(subtyping);
		this.cFile = cFile;
	}

	public void visitModule(WyilFile wf) {
		List<Declaration> decls = cFile.getDeclarations();
		// Add includes
		decls.add(new Declaration.Include("stdio.h"));
		decls.add(new Declaration.Include("stdbool.h"));
		decls.add(new Declaration.Include("stdint.h"));
		decls.add(new Declaration.Include("assert.h"));
		// Translate local units
		for (Decl.Unit unit : wf.getModule().getUnits()) {
			for (Decl decl : unit.getDeclarations()) {
				CLangFile.Declaration d = visitDeclaration(decl);
				if (d != null) {
					decls.add(d);
				}
			}
		}
	}

	public void addEntryPoint(Trie entry) {
		String name = entry.toString().replace("/", "_");
		ArrayList<Declaration.Parameter> params = new ArrayList<>();
		Statement.Block body = new Statement.Block(INVOKE(name, Arrays.asList()), RETURN(INT_CONST(0)));
		Declaration main = new Declaration.Method(INT(), "main", params, body);
		// Add main declaration
		cFile.getDeclarations().add(main);
	}

	// ====================================================================================
	// Declarations
	// ====================================================================================

	@Override
	public Declaration constructImport(Import d) {
		return null;
	}

	@Override
	public Declaration constructType(WyilFile.Decl.Type d, List<Expression> invariant, Type type) {
		String name = d.getName().get();
		return TYPEDEF(name,type);
	}

	@Override
	public Declaration constructStaticVariable(StaticVariable d, Expression initialiser) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Declaration constructProperty(Property d, Statement body) {
		String name = toMangledName(d);
		ArrayList<Declaration.Parameter> params = new ArrayList<>();
		// Translate return
		Type returnType = visitType(d.getType().getReturn());
		// Translate parameters
		for(Decl.Variable v : d.getParameters()) {
			Type type = visitType(v.getType());
			String vn = v.getName().get();
			params.add(new Declaration.Parameter(type,vn));
		}
		return new Declaration.Method(returnType, name, params, (Statement.Block) body);
	}

	@Override
	public Declaration constructVariant(Variant decl, List<Expression> clauses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Declaration constructFunction(Function d, List<Expression> precondition, List<Expression> postcondition, Statement body) {
		String name = toMangledName(d);
		ArrayList<Declaration.Parameter> params = new ArrayList<>();
		// Translate return
		Type returnType = visitType(d.getType().getReturn());
		// Translate parameters
		for(Decl.Variable v : d.getParameters()) {
			Type type = visitType(v.getType());
			String vn = v.getName().get();
			params.add(new Declaration.Parameter(type,vn));
		}
		return new Declaration.Method(returnType, name, params, (Statement.Block) body);
	}

	@Override
	public Declaration constructMethod(Method d, List<Expression> precondition, List<Expression> postcondition, Statement body) {
		String name = toMangledName(d);
		ArrayList<Declaration.Parameter> params = new ArrayList<>();
		// Translate return
		Type returnType = visitType(d.getType().getReturn());
		// Translate parameters
		for(Decl.Variable v : d.getParameters()) {
			Type type = visitType(v.getType());
			String vn = v.getName().get();
			params.add(new Declaration.Parameter(type,vn));
		}
		return new Declaration.Method(returnType, name, params, (Statement.Block) body);
	}

	@Override
	public Expression constructLambda(Lambda decl, Expression body) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructAssert(Assert stmt, Expression condition) {
		return INVOKE("assert", Arrays.asList(condition));
	}

	@Override
	public Statement constructAssign(Assign stmt, List<Expression> lvals, List<Expression> rvals) {
		if(lvals.size() != rvals.size()) {
			// TODO: support destructuring assignments.
			throw new IllegalArgumentException();
		}
		ArrayList<Statement> steps = new ArrayList<>();
		for(int i=0;i!=lvals.size();++i) {
			Expression lval = lvals.get(i);
			Expression rval = rvals.get(i);
			steps.add(ASSIGN(lval,rval));
		}
		if(steps.size() == 1) {
			return steps.get(0);
		} else {
			return new Statement.Block(steps);
		}
	}

	@Override
	public Statement constructAssume(Assume stmt, Expression condition) {
		return INVOKE("assert", Arrays.asList(condition));
	}

	@Override
	public Statement constructBlock(Block stmt, List<Statement> stmts) {
		return new Statement.Block(stmts);
	}

	@Override
	public Statement constructBreak(Break stmt) {
		return BREAK();
	}

	@Override
	public Statement constructContinue(Continue stmt) {
		return CONTINUE();
	}

	@Override
	public Statement constructDebug(Debug stmt, Expression operand) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Statement constructDoWhile(DoWhile stmt, Statement body, Expression condition, List<Expression> invariant) {
		return DOWHILE(condition, body);
	}

	@Override
	public Statement constructFail(Fail stmt) {
		// Force an assertion failure.
		return INVOKE("assert", Arrays.asList(BOOL_CONST(false)));
	}

	@Override
	public Statement constructFor(For stmt, Pair<Expression, Expression> range, List<Expression> invariant,
			Statement body) {
		String name = stmt.getVariable().getName().get();
		Expression var = VAR(name);
		Statement initialiser = new Declaration.Variable(INT(), name, range.first());
		// FIXME: there may be an inconsistency here, since we probably should be
		// evaluating the range before the loop.
		Expression condition = LT(var, range.second());
		Statement increment = ASSIGN(var, ADD(var, INT_CONST(1)));
		return FOR(initialiser, condition, increment, body);
	}

	@Override
	public Statement constructIfElse(IfElse stmt, Expression condition, Statement trueBranch, Statement falseBranch) {
		return IF(condition, trueBranch, falseBranch);
	}

	@Override
	public Statement constructInitialiser(Initialiser stmt, Type type, Expression initialiser) {
		Tuple<WyilFile.Decl.Variable> vars = stmt.getVariables();
		// TODO: improve this!
		if(vars.size() != 1) {
			throw new IllegalArgumentException();
		}
		//
		String name = vars.get(0).getName().get();
		return new CLangFile.Declaration.Variable(type, name, initialiser);
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
		return RETURN(ret);
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
		return WHILE(condition, body);
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
		String field = expr.getField().get();
		return FIELD_ACCESS(source,field);
	}

	@Override
	public Expression constructRecordUpdate(RecordUpdate expr, Expression source, Expression value) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructTupleInitialiserLVal(TupleInitialiser expr, List<Expression> source) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructVariableAccessLVal(VariableAccess expr) {
		String name = expr.getVariableDeclaration().getName().get();
		return VAR(name);
	}

	@Override
	public Expression constructStaticVariableAccessLVal(StaticVariableAccess expr) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructArrayAccess(ArrayAccess expr, Expression source, Expression index) {
		return ARRAY_ACCESS(source,index);
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
		Expression e = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			e = BIT_AND(e,operands.get(i));
		}
		return e;

	}

	@Override
	public Expression constructBitwiseOr(BitwiseOr expr, List<Expression> operands) {
		Expression e = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			e = BIT_OR(e,operands.get(i));
		}
		return e;
	}

	@Override
	public Expression constructBitwiseXor(BitwiseXor expr, List<Expression> operands) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException();
	}

	@Override
	public Expression constructBitwiseShiftLeft(BitwiseShiftLeft expr, Expression lhs, Expression rhs) {
		return SHL(lhs,rhs);
	}

	@Override
	public Expression constructBitwiseShiftRight(BitwiseShiftRight expr, Expression lhs, Expression rhs) {
		return SHR(lhs,rhs);
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
			return BOOL_CONST(b.get());
		} else if(v instanceof Value.Byte) {
			Value.Byte b = (Value.Byte) v;
			return HEX_CONST(b.get() & 0xFF);
		} else if(v instanceof Value.Int) {
			Value.Int b = (Value.Int) v;
			// TODO: clearly a hack for now.
			return INT_CONST(b.get().intValueExact());
		} else {
			// TODO Auto-generated method stub
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Expression constructDereference(Dereference expr, Expression operand) {
		return DEREFERENCE(operand);
	}

	@Override
	public Expression constructFieldDereference(FieldDereference expr, Expression operand) {
		String field = expr.getField().get();
		return FIELD_DEREFERENCE(operand, field);
	}

	@Override
	public Expression constructEqual(Equal expr, Expression lhs, Expression rhs) {
		return EQ(lhs, rhs);
	}

	@Override
	public Expression constructIntegerLessThan(IntegerLessThan expr, Expression lhs, Expression rhs) {
		return LT(lhs, rhs);
	}

	@Override
	public Expression constructIntegerLessThanOrEqual(IntegerLessThanOrEqual expr, Expression lhs, Expression rhs) {
		return LTEQ(lhs, rhs);
	}

	@Override
	public Expression constructIntegerGreaterThan(IntegerGreaterThan expr, Expression lhs, Expression rhs) {
		return GT(lhs, rhs);
	}

	@Override
	public Expression constructIntegerGreaterThanOrEqual(IntegerGreaterThanOrEqual expr, Expression lhs, Expression rhs) {
		return GTEQ(lhs, rhs);
	}

	@Override
	public Expression constructIntegerNegation(IntegerNegation expr, Expression operand) {
		return NEG(operand);
	}

	@Override
	public Expression constructIntegerAddition(IntegerAddition expr, Expression lhs, Expression rhs) {
		return ADD(lhs,rhs);
	}

	@Override
	public Expression constructIntegerSubtraction(IntegerSubtraction expr, Expression lhs, Expression rhs) {
		return SUB(lhs,rhs);
	}

	@Override
	public Expression constructIntegerMultiplication(IntegerMultiplication expr, Expression lhs, Expression rhs) {
		return MUL(lhs,rhs);
	}

	@Override
	public Expression constructIntegerDivision(IntegerDivision expr, Expression lhs, Expression rhs) {
		return DIV(lhs,rhs);
	}

	@Override
	public Expression constructIntegerRemainder(IntegerRemainder expr, Expression lhs, Expression rhs) {
		return REM(lhs,rhs);
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
		Expression e = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			e = AND(e,operands.get(i));
		}
		return e;
	}

	@Override
	public Expression constructLogicalImplication(LogicalImplication expr, Expression lhs, Expression rhs) {
		return OR(NOT(lhs),rhs);
	}

	@Override
	public Expression constructLogicalIff(LogicalIff expr, Expression lhs, Expression rhs) {
		return EQ(lhs,rhs);
	}

	@Override
	public Expression constructLogicalNot(LogicalNot expr, Expression operand) {
		return NOT(operand);
	}

	@Override
	public Expression constructLogicalOr(LogicalOr expr, List<Expression> operands) {
		Expression e = operands.get(0);
		for(int i=1;i!=operands.size();++i) {
			e = OR(e,operands.get(i));
		}
		return e;
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
		String name = expr.getBinding().getLink().getName().toString();
		return INVOKE(name, arguments);
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
		return NEQ(lhs, rhs);
	}

	@Override
	public Expression constructOld(Old expr, Expression operand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression constructRecordAccess(RecordAccess expr, Expression source) {
		String field = expr.getField().get();
		return FIELD_ACCESS(source,field);
	}

	@Override
	public Expression constructRecordInitialiser(RecordInitialiser expr, List<Pair<String,Expression>> operands) {
		return INITIALISER(operands);
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
		String name = expr.getVariableDeclaration().getName().get();
		return VAR(name);
	}


	@Override
	public Type constructArrayType(WyilFile.Type.Array type, Type element) {
		return POINTER(element);
	}

	@Override
	public Type constructBoolType(WyilFile.Type.Bool type) {
		return BOOL();
	}

	@Override
	public Type constructByteType(WyilFile.Type.Byte type) {
		return UINT(8);
	}

	@Override
	public Type constructIntType(WyilFile.Type.Int type) {
		return INT();
	}

	@Override
	public Type constructNominalType(WyilFile.Type.Nominal type) {
		String name = type.getLink().getName().toString();
		return NOMINAL(name);
	}

	@Override
	public Type constructRecordType(WyilFile.Type.Record type, List<Pair<Type,String>> types) {
		return STRUCT(types);
	}

	@Override
	public Type constructReferenceType(WyilFile.Type.Reference type, Type element) {
		return POINTER(element);
	}

	@Override
	public Type constructUnionType(WyilFile.Type.Union type, List<Type> element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type constructVoidType(WyilFile.Type.Void type) {
		return VOID();
	}


	@Override
	public Statement applyImplicitCoercion(wyil.lang.WyilFile.Type target, wyil.lang.WyilFile.Type source,
			Statement expr) {
		// TODO Auto-generated method stub
		return null;
	}

	// =======================================================================================================
	// Helpers
	// =======================================================================================================

	/**
     * Determine the appropriate mangled string for a given named declaration. This is critical to ensuring that
     * overloaded declarations do not clash.
     *
     * @param decl The declaration for which the mangle is being determined
     * @param type The concrete type for the declaration in question (as this may differ from the declared type when
     *             type parameters are included).
     * @return
     */
    private String toMangledName(WyilFile.Decl.Named<?> decl, WyilFile.Type type) {
        // Determine whether this is an exported symbol or not
        boolean exported = decl.getModifiers().match(WyilFile.Modifier.Export.class) != null;
        // Check whether mangling applies
        if(mangling) {
            // Construct base name
            String name = decl.getQualifiedName().toString().replace("::", "_");
            // Add type mangles for non-exported symbols only
            return exported ? name : (name + "_" + mangler.getMangle(type));
        } else {
            return decl.getName().toString();
        }
    }

    /**
     * Provides a default argument based on declaration's declared type.
     *
     * @param decl
     * @return
     */
    private String toMangledName(WyilFile.Decl.Named<?> decl) {
        return toMangledName(decl, decl.getType());
    }
}