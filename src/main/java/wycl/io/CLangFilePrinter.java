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
import wycl.core.CLangFile.Statement;
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

	private void writeStatement(int indent, Statement stmt) {
		if(stmt instanceof Statement.Block) {
			writeBlock(indent,(Statement.Block) stmt);
		} else if(stmt instanceof Statement.Skip) {
			writeSkip(indent,(Statement.Skip) stmt);
		}
	}

	private void writeBlock(int indent, Statement.Block block) {
		out.println(" {");
		for(Statement stmt : block.getTerms()) {
			writeStatement(indent+1,stmt);
		}
		tab(indent);out.print("}");
	}

	private void writeSkip(int indent, Statement.Skip stmt) {
		tab(indent);
		out.println("// skip");
	}

	private void tab(int indent) {
		for (int i = 0; i != indent; ++i) {
			out.print("   ");
		}
	}
}
