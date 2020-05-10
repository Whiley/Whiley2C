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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import wybs.lang.Build;
import wybs.lang.Build.Meter;
import wybs.lang.Build.Project;
import wybs.util.AbstractBuildTask;
import wycl.core.CLangFile;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyil.lang.WyilFile;

public class CLangCompileTask extends AbstractBuildTask<WyilFile, CLangFile>{

	public CLangCompileTask(Project project, Entry<CLangFile> target, Path.Entry<WyilFile> sources) {
		super(project, target, Arrays.asList(sources));
	}

	@Override
	public Function<Meter, Boolean> initialise() throws IOException {
		// Extract target and source files for compilation. This is the component which
		// requires I/O.
		CLangFile jsf = target.read();
		WyilFile wyf = sources.get(0).read();
		// Construct the lambda for subsequent execution. This will eventually make its
		// way into some kind of execution pool, possibly for concurrent execution with
		// other tasks.
		return (Build.Meter meter) -> execute(meter, jsf, wyf);
	}


	public boolean execute(Build.Meter meter, CLangFile target, WyilFile source) {
		meter = meter.fork("CLangCompiler");
		// FIXME: this is a fairly temporary solution at the moment which just
		// turns the WyIL file directly into a string. A more useful solution
		// will be to generate an intermediate file representing JavaScript in
		// an AST. This would enable, for example, better support for different
		// standards. It would also enable minification, and allow support for
		// different module systems (e.g. CommonJS).
		new CLangCompiler(meter,target).visitModule(source);
		//
		meter.done();
		// How can this fail?
		return true;
	}
}
