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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wyfs.lang.Content;
import wyfs.lang.Path;
import wycl.core.CLangFile;
import wycl.io.CLangFilePrinter;

public class CLangFile {
	// =========================================================================
	// Content Type
	// =========================================================================

	/**
	 * Responsible for identifying and reading/writing WyilFiles. The normal
	 * extension is ".wyil" for WyilFiles.
	 */
	public static final Content.Type<CLangFile> ContentType = new Content.Type<CLangFile>() {
		public Path.Entry<CLangFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<CLangFile>) e;
			}
			return null;
		}

		@Override
		public CLangFile read(Path.Entry<CLangFile> e, InputStream input) throws IOException {
			return new CLangFile();
		}

		@Override
		public void write(OutputStream output, CLangFile jf) throws IOException {
			new CLangFilePrinter(output).write(jf);
		}

		@Override
		public String toString() {
			return "Content-Type: c";
		}

		@Override
		public String getSuffix() {
			return "c";
		}
	};

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
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	public interface Expression extends Term {

	}

	// =========================================================================
	// Types
	// =========================================================================

	public interface Type {

	}
}

