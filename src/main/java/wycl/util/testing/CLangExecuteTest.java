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
import java.util.Map;

import wycc.util.TextFile;
import wycc.util.Trie;
import wycc.util.testing.TestFile;
import wycc.util.testing.TestFile.Error;
import wycc.util.testing.TestStage;
import wycl.util.StreamGrabber;
import wyil.lang.WyilFile;

public class CLangExecuteTest implements TestStage {

	@Override
	public Result apply(Trie path, Path dir, Map<Trie, TextFile> state, TestFile tf) throws IOException {
		boolean ignored = tf.get(Boolean.class, "c.execute.ignore").orElse(false);
		// Test was expected to compile, so attempt to run the code.
		String unit = tf.get(String.class, "main.file").orElse("main");
		//
		try {
			Path executable = dir.resolve(path.toString());
			Process p = Runtime.getRuntime().exec(executable.toString());
			StringBuffer syserr = new StringBuffer();
			StringBuffer sysout = new StringBuffer();
			new StreamGrabber(p.getErrorStream(), syserr);
			new StreamGrabber(p.getInputStream(), sysout);
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				System.err.println(syserr); // propagate anything from the error
				TestFile.Coordinate c = new TestFile.Coordinate(0, new TestFile.Range(0, 0));
				return new Result(ignored, new Error(WyilFile.INTERNAL_FAILURE, Trie.fromString(unit), c));
			}
			// stream
			return new Result(ignored, new Error[0]);
		} catch (Exception ex) {
			ex.printStackTrace();
			TestFile.Coordinate c = new TestFile.Coordinate(0, new TestFile.Range(0, 0));
			return new Result(ignored, new Error(WyilFile.INTERNAL_FAILURE, Trie.fromString(unit), c));
		}
	}

	@Override
	public Error[] filter(Error[] errors) {
		return new Error[0];
	}

	@Override
	public boolean required() {
		return false;
	}
}
