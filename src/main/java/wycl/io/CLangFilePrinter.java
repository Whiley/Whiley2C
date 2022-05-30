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

import wycl.core.CLangFile;
import wycl.core.CLangFile.Declaration;
import wycl.core.CLangFile.Expression;
import wycl.core.CLangFile.Statement;
import wycl.core.CLangFile.Type;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;

public class CLangFilePrinter {
	private final PrintWriter out;

	public CLangFilePrinter(OutputStream output) {
		this.out = new PrintWriter(output);
	}

	public void write(CLangFile cf) {
		// TODO Auto-generated method stub
		for(Declaration d : cf.getDeclarations()) {
			write(0,d);
		}
		out.flush();
		out.close();
	}

	private void write(int indent, Declaration d) {
		if (d instanceof Declaration.Method) {
			write(indent, (Declaration.Method) d);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void write(int indent, Declaration.Method d) {
		tab(indent);
		out.print("void " + d.getName() + "()");
		writeBlock(indent, d.getBody());
	}

	private void writeVariableDeclaration(int indent, Declaration.Variable d) {
		tab(indent);
		writeType(d.getType());
		out.print(" ");
		out.print(d.getName());
		if(d.getInitialiser() != null) {
			out.print(" = ");
			writeExpression(d.getInitialiser());
		}
		out.println(";");
	}

	private void writeStatement(int indent, Statement stmt) {
		if(stmt instanceof Declaration.Variable) {
			writeVariableDeclaration(indent, (Declaration.Variable) stmt);
		} else if(stmt instanceof Statement.Block) {
			writeBlock(indent,(Statement.Block) stmt);
		} else if(stmt instanceof Statement.If) {
			writeIf(indent,(Statement.If) stmt);
		} else if(stmt instanceof Statement.Skip) {
			writeSkip(indent,(Statement.Skip) stmt);
		} else if(stmt instanceof Statement.While) {
			writeWhile(indent,(Statement.While) stmt);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeBlock(int indent, Statement.Block block) {
		out.println(" {");
		for(Statement stmt : block.getTerms()) {
			writeStatement(indent+1,stmt);
		}
		tab(indent);out.print("}");
	}

	private void writeIf(int indent, Statement.If stmt) {
		tab(indent);
		out.print("if(");
		writeExpression(stmt.getCondition());
		out.print(") ");
		writeStatement(indent, stmt.getTrueBranch());
		if(stmt.getFalseBranch() != null) {
			writeStatement(indent, stmt.getFalseBranch());
		}
		out.println();
	}

	private void writeWhile(int indent, Statement.While stmt) {
		tab(indent);
		out.print("while(");
		writeExpression(stmt.getCondition());
		out.print(") ");
		writeStatement(indent, stmt.getBody());
		out.println();
	}

	private void writeSkip(int indent, Statement.Skip stmt) {
		tab(indent);
		out.println("// skip");
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
		if(expr instanceof Expression.Infix) {
			writeInfix((Expression.Infix) expr);
		} else if(expr instanceof Expression.IntConstant) {
			writeIntConstant((Expression.IntConstant) expr);
		} else if(expr instanceof Expression.Var) {
			writeVariableAccess((Expression.Var) expr);
		}
		else {
			throw new IllegalArgumentException();
		}
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

	public void writeVariableAccess(Expression.Var expr) {
		out.print(expr.getName());
	}

	// ============================================================
	// Types
	// ============================================================

	private void writeType(Type type) {
		if(type instanceof Type.Int) {
			writeTypeInt((Type.Int)type);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeTypeInt(Type.Int type) {
		out.print("int");
	}

	private void tab(int indent) {
		for (int i = 0; i != indent; ++i) {
			out.print("   ");
		}
	}
}
