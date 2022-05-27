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


import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import wyc.util.testing.WhileyCompileTest;
import wycc.util.Trie;
import wycc.util.testing.TestManager;
import wycc.util.testing.TestManager.Result;
import wycl.util.testing.CLangCompileTest;

/**
 * Run through all valid test cases with verification enabled. Since every test
 * file is valid, a successful test occurs when the compiler succeeds and, when
 * executed, the compiled file produces the expected output. Note that an
 * internal failure does not count as a valid pass, and indicates the test
 * exposed some kind of compiler bug.
 *
 * @author David J. Pearce
 *
 */
public class WhileyCompilerTests {
	public enum Error {
		OK, // Test compiled and verified correctly
		FAILED_COMPILE, // Test did not compile
		FAILED_VERIFY, // Test failed boogie
		EXCEPTION,	// internal failure
	}
	/**
	 * Configure Timeout to use for Boogie (in seconds)
	 */
	public final static int TIMEOUT = 60;
	/**
	 * Configure debug mode which (when enabled) produces easier to read Boogie output.  This should not be enabled by default.
	 */
	private final static boolean DEBUG = false;
	/**
	 * Configure Boogie to use an alternative SMT solver. This should be
	 * <code>null</code> in the general case, as this signals boogie to use the
	 * default (i.e. Z3). However, for debugging, you can override it.
	 */
	private final static String PROVER_NAME = null;
	/**
	 * The directory containing the test files.
	 */
	public final static Path WHILEY_SRC_DIR = Path.of("tests");

	public final static TestManager manager = new TestManager(WHILEY_SRC_DIR, new WhileyCompileTest(),
			new CLangCompileTest());

	// ======================================================================
	// Test Harness
	// ======================================================================

	@ParameterizedTest
	@MethodSource("sourceFiles")
 	public void mainTests(Trie path) throws IOException {
		TestManager.Result r = manager.run(path);
		//
		if(r == Result.IGNORED) {
			Assumptions.assumeTrue(false, "Test " + path + " skipped");
		} else if(r == Result.FAILURE) {
			fail("test failure for reasons unknown");
		} else if(r == Result.INVALIDIGNORED) {
			fail("test should not be marked as ignored");
		}
	}

	// Here we enumerate all available test cases.
	private static Stream<Trie> sourceFiles() throws IOException {
		return readTestFiles(WHILEY_SRC_DIR, n -> true);
	}

	// ======================================================================
	// Debugging
	// ======================================================================

	@ParameterizedTest
	@MethodSource("debugFiles")
	public void debugTests(Trie path) throws IOException {
		// Enable debugging
		manager.setDebug(true);
		// Run the test
		mainTests(path);
	}

	// Here we enumerate all available test cases.
	private static Stream<Trie> debugFiles() throws IOException {
//		return readTestFiles(WHILEY_SRC_DIR, in(74,405,406,407,408,410,413,416,417,418,419,423,429,462,483,484,602,603,616,1089));
		return readTestFiles(WHILEY_SRC_DIR, atleast(1430));
	}

	// ======================================================================
	// Data sources
	// ======================================================================

	public static Stream<Trie> readTestFiles(java.nio.file.Path dir, Predicate<String> filter) throws IOException {
		ArrayList<Trie> testcases = new ArrayList<>();
		//
		Files.walk(dir,1).forEach(f -> {
			if (f.toString().endsWith(".test") && filter.test(f.getFileName().toString())) {
				// Determine the test name
				testcases.add(extractTestName(f));
			}
		});
		// Sort the result by filename
		Collections.sort(testcases);
		//
		return testcases.stream();
	}

	private static Predicate<String> in(int... tests) {
		return n -> {
			int t = Integer.parseInt(n.replace(".test", ""));
			for(int i=0;i!=tests.length;++i) {
				if(t == tests[i]) {
					return true;
				}
			}
			return false;
		};
	}

	private static Predicate<String> equals(int testNumber) {
		return n -> Integer.parseInt(n.replace(".test", "")) == testNumber;
	}

	private static Predicate<String> atleast(int testNumber) {
		return n -> Integer.parseInt(n.replace(".test", "")) >= testNumber;
	}

	private static Predicate<String> atmost(int testNumber) {
		return n -> Integer.parseInt(n.replace(".test", "")) <= testNumber;
	}

	private static Trie extractTestName(java.nio.file.Path f) {
		return Trie.fromString(f.getFileName().toString().replace(".test",""));
	}
}
