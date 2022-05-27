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
import java.util.Collection;
import java.util.List;

import wycc.util.Trie;
import wycl.core.CLangFile;
import wyil.lang.WyilFile;


public class CLangCompileTask {
	/**
	 * Identifier for target of this build task.
	 */
	private Trie target = Trie.fromString("main");
	/**
	 * The set of source files that this task will compiler from.
	 */
	private final List<WyilFile> sources = new ArrayList<>();

	public CLangCompileTask setTarget(Trie target) {
		this.target = target;
		return this;
	}

	public CLangCompileTask addSource(WyilFile f) {
		this.sources.add(f);
		return this;
	}

	public CLangCompileTask addSources(Collection<WyilFile> fs) {
		this.sources.addAll(fs);
		return this;
	}

	public CLangFile run() {
		// Construct initial (empty) JavaScript file
		CLangFile cFile = new CLangFile();
		// Process source files one by one
		for (WyilFile i : sources) {
			new CLangCompiler(cFile).visitModule(i);
		}
		//
		return cFile;
	}
}
