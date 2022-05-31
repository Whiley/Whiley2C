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

import wycl.core.CLangFile;
import wycl.core.CLangFile.Declaration;
import wycl.core.CLangFile.Expression;
import wycl.core.CLangFile.Statement;
import wycl.core.CLangFile.Type;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Stmt;

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
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void writeInclude(int indent, Declaration.Include d) {
		tab(indent);
		out.println("#include <" + d.getInclude() + ">");
	}

	private void writeMethod(int indent, Declaration.Method d) {
		tab(indent);
		out.print("void " + d.getName() + "()");
		writeBlock(indent, d.getBody());
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
		if(expr instanceof Expression.Infix) {
			writeInfix((Expression.Infix) expr);
		} else if(expr instanceof Expression.IntConstant) {
			writeIntConstant((Expression.IntConstant) expr);
		} else if(expr instanceof Expression.Invoke) {
			writeInvoke((Expression.Invoke) expr);
		} else if(expr instanceof Expression.Var) {
			writeVariableAccess((Expression.Var) expr);
		} else {
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
