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
package wycl.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import wycc.util.Pair;
import wycl.core.CLangFile;
import wycl.core.CLangFile.Declaration;
import wycl.core.CLangFile.Expression;
import wycl.core.CLangFile.Statement;
import wycl.core.CLangFile.Type;

public class CLangFilePrinter {
	private final PrintWriter out;

	public CLangFilePrinter(OutputStream output) {
		this.out = new PrintWriter(output);
	}

	public void write(CLangFile cf) {
		for(Declaration d : cf.getDeclarations()) {
			write(0,d);
		}
		out.flush();
		out.close();
	}

	private void write(int indent, Declaration d) {
		if (d instanceof Declaration.Include) {
			writeInclude(indent, (Declaration.Include) d);
		} else if (d instanceof Declaration.Method) {
			writeMethod(indent, (Declaration.Method) d);
		} else if (d instanceof Declaration.TypeDef) {
			writeTypeDef(indent, (Declaration.TypeDef) d);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeInclude(int indent, Declaration.Include d) {
		tab(indent);
		out.println("#include <" + d.getInclude() + ">");
	}

	private void writeMethod(int indent, Declaration.Method d) {
		List<Declaration.Parameter> params = d.getParameters();
		tab(indent);
		writeType(d.getReturnType());
		out.print(" ");
		out.print(d.getName());
		out.print("(");
		for(int i=0;i!=params.size();++i) {
			Declaration.Parameter ith = params.get(i);
			if(i != 0) {
				out.print(", ");
			}
			writeType(ith.getType());
			out.print(" ");
			out.print(ith.getName());
		}
		out.print(")");
		writeBlock(indent, d.getBody());
		out.println();
	}

	private void writeTypeDef(int indent, Declaration.TypeDef d) {
		tab(indent);
		out.print("typedef ");
		writeType(d.getType());
		out.print(" ");
		out.print(d.getName());
		out.println(";");
	}

	private void writeVariableDeclaration(int indent, Declaration.Variable d) {
		writeType(d.getType());
		out.print(" ");
		out.print(d.getName());
		if(d.getInitialiser() != null) {
			out.print(" = ");
			writeExpression(d.getInitialiser());
		}
	}

	private void writeStatement(int indent, Statement stmt) {
		tab(indent);
		writeInternalStatement(indent,stmt);
		if(stmt instanceof Statement.Block || stmt instanceof Statement.For || stmt instanceof Statement.If || stmt instanceof Statement.While ) {
			out.println();
		} else {
			out.println(";");
		}
	}

	private void writeInternalStatement(int indent, Statement stmt) {
		if(stmt instanceof Declaration.Variable) {
			writeVariableDeclaration(indent, (Declaration.Variable) stmt);
		} else if(stmt instanceof Statement.Assign) {
			writeAssign(indent,(Statement.Assign) stmt);
		} else if(stmt instanceof Statement.Block) {
			writeBlock(indent,(Statement.Block) stmt);
		} else if(stmt instanceof Statement.Break) {
			writeBreak(indent,(Statement.Break) stmt);
		} else if(stmt instanceof Statement.Continue) {
			writeContinue(indent,(Statement.Continue) stmt);
		} else if(stmt instanceof Statement.DoWhile) {
			writeDoWhile(indent,(Statement.DoWhile) stmt);
		} else if(stmt instanceof Statement.For) {
			writeFor(indent,(Statement.For) stmt);
		} else if(stmt instanceof Statement.If) {
			writeIf(indent,(Statement.If) stmt);
		} else if(stmt instanceof Statement.Return) {
			writeReturn(indent,(Statement.Return) stmt);
		} else if(stmt instanceof Statement.Skip) {
			writeSkip(indent,(Statement.Skip) stmt);
		} else if(stmt instanceof Statement.While) {
			writeWhile(indent,(Statement.While) stmt);
		} else if(stmt instanceof Expression) {
			writeExpression((Expression) stmt);
		} else {
			throw new IllegalArgumentException("unknown statement: " + stmt.getClass().getName());
		}
	}

	private void writeAssign(int indent, Statement.Assign stmt) {
		writeExpression(stmt.getLeftHandSide());
		out.print(" = ");
		writeExpression(stmt.getRightHandSide());
	}

	private void writeBlock(int indent, Statement.Block block) {
		out.println("{");
		for(Statement stmt : block.getTerms()) {
			writeStatement(indent+1,stmt);
		}
		tab(indent);out.print("}");
	}

	private void writeBreak(int indent, Statement.Break block) {
		out.print("break");
	}

	private void writeContinue(int indent, Statement.Continue block) {
		out.print("continue");
	}

	private void writeDoWhile(int indent, Statement.DoWhile stmt) {
		out.print("do ");
		writeStatement(indent, stmt.getBody());
		out.print("while(");
		writeExpression(stmt.getCondition());
		out.print(")");
	}

	private void writeFor(int indent, Statement.For stmt) {
		out.print("for(");
		writeInternalStatement(0,stmt.getInitialiser());
		out.print("; ");
		writeExpression(stmt.getCondition());
		out.print("; ");
		writeInternalStatement(0, stmt.getIncrement());
		out.print(") ");
		writeInternalStatement(indent, stmt.getBody());
	}

	private void writeIf(int indent, Statement.If stmt) {
		out.print("if(");
		writeExpression(stmt.getCondition());
		out.print(") ");
		writeInternalStatement(indent, stmt.getTrueBranch());
		if(stmt.getFalseBranch() != null) {
			out.print(" else ");
			writeInternalStatement(indent, stmt.getFalseBranch());
		}
	}

	private void writeReturn(int indent, Statement.Return stmt) {
		out.print("return");
		if(stmt.getOperand() != null) {
			out.print(" ");
			writeExpression(stmt.getOperand());
		}
	}

	private void writeWhile(int indent, Statement.While stmt) {
		out.print("while(");
		writeExpression(stmt.getCondition());
		out.print(") ");
		writeStatement(indent, stmt.getBody());
	}

	private void writeSkip(int indent, Statement.Skip stmt) {
		out.print("/* skip */");
	}

	private void writeBracketedExpression(Expression expr) {
		if(expr.requiresParenthesis()) {
			out.print("(");
			writeExpression(expr);
			out.print(")");
		} else {
			writeExpression(expr);
		}
	}

	private void writeExpression(Expression expr) {
		if(expr instanceof Expression.ArrayAccess) {
			writeArrayAccess((Expression.ArrayAccess) expr);
		} else if(expr instanceof Expression.BoolConstant) {
			writeBoolConstant((Expression.BoolConstant) expr);
		} else if(expr instanceof Expression.FieldAccess) {
			writeFieldAccess((Expression.FieldAccess) expr);
		} else if(expr instanceof Expression.Infix) {
			writeInfix((Expression.Infix) expr);
		} else if(expr instanceof Expression.IntConstant) {
			writeIntConstant((Expression.IntConstant) expr);
		} else if(expr instanceof Expression.Invoke) {
			writeInvoke((Expression.Invoke) expr);
		} else if(expr instanceof Expression.DesignatedInitialiser) {
			writeDesignatedInitialiser((Expression.DesignatedInitialiser) expr);
		} else if(expr instanceof Expression.Var) {
			writeVariableAccess((Expression.Var) expr);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeArrayAccess(Expression.ArrayAccess expr) {
		writeBracketedExpression(expr.getSource());
		out.print("[");
		writeExpression(expr.getIndex());
		out.print("]");
	}

	private void writeBoolConstant(Expression.BoolConstant expr) {
		out.print(expr.getConstant());
	}

	private void writeFieldAccess(Expression.FieldAccess expr) {
		writeBracketedExpression(expr.getOperand());
		out.print(".");
		out.print(expr.getField());
	}

	private void writeInfix(Expression.Infix expr) {
		writeBracketedExpression(expr.getLeftHandSide());
		out.print(" ");
		out.print(expr.getOperatorString());
		out.print(" ");
		writeBracketedExpression(expr.getRightHandSide());
	}

	private void writeIntConstant(Expression.IntConstant expr) {
		out.print(expr.getConstant());
	}

	private void writeInvoke(Expression.Invoke expr) {
		List<Expression> args = expr.getArguments();
		out.print(expr.getName());
		out.print("(");
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeExpression(args.get(i));
		}
		out.print(")");
	}

	private void writeDesignatedInitialiser(Expression.DesignatedInitialiser expr) {
		List<Pair<String,Expression>> fields = expr.getFields();
		out.print("{");
		for(int i=0;i!=fields.size();++i) {
			if(i != 0) {
				out.print(", ");
			}
			out.print(".");
			out.print(fields.get(i).first());
			out.print(" = ");
			writeExpression(fields.get(i).second());
		}
		out.print("}");
	}

	public void writeVariableAccess(Expression.Var expr) {
		out.print(expr.getName());
	}

	// ============================================================
	// Types
	// ============================================================

	private void writeType(Type type) {
		if (type instanceof Type.Bool) {
			writeTypeBool((Type.Bool) type);
		} else if (type instanceof Type.Int) {
			writeTypeInt((Type.Int) type);
		} else if (type instanceof Type.Nominal) {
			writeTypeNominal((Type.Nominal) type);
		} else if (type instanceof Type.Pointer) {
			writeTypePointer((Type.Pointer) type);
		} else if (type instanceof Type.Struct) {
			writeTypeStruct((Type.Struct) type);
		} else if (type instanceof Type.Void) {
			writeTypeVoid((Type.Void) type);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeTypePointer(Type.Pointer type) {
		writeType(type.getElement());
		out.print("*");
	}

	private void writeTypeBool(Type.Bool type) {
		out.print("bool");
	}

	private void writeTypeInt(Type.Int type) {
		if(type.hasFixedWidth()) {
			out.print(type.isSigned() ? "int" : "uint");
			out.print(type.getWidth());
			out.print("_t");
		} else if(type.isSigned()) {
			out.print("int");
		} else {
			out.print("unsigned int");
		}
	}

	private void writeTypeNominal(Type.Nominal type) {
		out.print(type.getName());
	}

	private void writeTypeStruct(Type.Struct type) {
		List<Pair<Type,String>> fields = type.getFields();
		out.print("struct { ");
		for(int i=0;i!=fields.size();++i) {
			writeType(fields.get(i).first());
			out.print(" ");
			out.print(fields.get(i).second());
			out.print(";");
		}
		out.print(" }");
	}

	private void writeTypeVoid(Type.Void type) {
		out.print("void");
	}

	private void tab(int indent) {
		for (int i = 0; i != indent; ++i) {
			out.print("   ");
		}
	}
}
