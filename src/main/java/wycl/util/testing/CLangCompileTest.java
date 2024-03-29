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
package wycl.util.testing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import wyc.util.testing.WhileyCompileTest;
import wycc.lang.Syntactic;
import wycc.util.*;
import wycc.util.testing.TestFile;
import wycc.util.testing.TestStage;
import wycc.util.testing.TestFile.Error;
import wycl.Main;
import wyil.lang.WyilFile;

public class CLangCompileTest implements TestStage {

	@Override
	public Result apply(Trie path, Path dir, Map<Trie, TextFile> state, TestFile tf) throws IOException {
		boolean ignored = tf.get(Boolean.class, "c.compile.ignore").orElse(false);
		// Test was expected to compile, so attempt to run the code.
		String unit = tf.get(String.class, "main.file").orElse("main");
		String method = tf.get(String.class, "main.method").orElse("test");
		Trie entry = Trie.fromString(unit).append(method);
		try {
			boolean r = new Main().setWyilDir(dir.toFile()).setCDir(dir.toFile()).setTarget(path).addSource(path).setEntry(entry).run();
			//
			if(r) {
				return new Result(ignored, new Error[0]);
			} else {
				TestFile.Coordinate c = new TestFile.Coordinate(0, new TestFile.Range(0, 0));
				return new Result(ignored, new Error(WyilFile.INTERNAL_FAILURE, Trie.fromString(unit), c));
			}
		} catch (Syntactic.Exception e) {
			e.printStackTrace();
			TestFile.Error err = WhileyCompileTest.toError(state, e);
			return new TestStage.Result(ignored, new TestFile.Error[] { err });
		} catch(Throwable e) {
			e.printStackTrace();
			TestFile.Coordinate c = new TestFile.Coordinate(0, new TestFile.Range(0, 0));
			return new Result(ignored, new Error(WyilFile.INTERNAL_FAILURE, Trie.fromString(unit), c));
		}
	}

	@Override
	public Error[] filter(Error[] errors) {
		return Arrays.asList(errors).stream().filter(m -> m.getErrorNumber() == 0).toArray(TestFile.Error[]::new);
	}

	@Override
	public boolean required() {
		return true;
	}
}
