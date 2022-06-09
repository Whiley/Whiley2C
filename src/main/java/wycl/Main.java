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
package wycl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import wycc.util.OptArg;
import wycc.util.Trie;
import wycl.core.CLangFile;
import wycl.io.CLangFilePrinter;
import wycl.tasks.CLangCompileTask;
import wycl.util.CC;
import wyil.lang.WyilFile;

public class Main {
	/**
	 * Destination directory of Wyil files.
	 */
	private File wyildir = new File(".");
	/**
	 * Destination directory of Wyil files.
	 */
	private File cdir = new File(".");
	/**
	 * List of source files.
	 */
	private List<Trie> sources = new ArrayList<>();
	/**
	 * Determine target filename.
	 */
	private Trie target = Trie.fromString("main");
	/**
	 * Determine entry point.
	 */
	private Trie entry = null;
	/**
	 * List of C files to include.
	 */
	private List<File> includes = new ArrayList<>();
	/**
	 * WyIL dependencies to include during compilation.
	 */
	private List<File> whileypath = Collections.EMPTY_LIST;

	public Main addSource(Trie source) {
		this.sources.add(source);
		return this;
	}

	public Main setTarget(Trie target) {
		this.target = target;
		return this;
	}

	public Main setWyilDir(File wyildir) {
		this.wyildir = wyildir;
		return this;
	}

	public Main setCDir(File jsdir) {
		this.cdir = jsdir;
		return this;
	}

	public Main setWhileyPath(List<File> whileypath) {
		this.whileypath = whileypath;
		return this;
	}

	public Main setEntry(Trie entry) {
		this.entry = entry;
		return this;
	}

	public boolean run() throws IOException {
		// Construct compile task
		CLangCompileTask task = new CLangCompileTask().setTarget(target).setEntry(entry);
		// Add sources
		for(Trie source : sources) {
			// Extract source file
			task.addSource(wyc.Compiler.readWyilFile(wyildir, source));
		}
		// Extract any dependencies from zips
		for(File dep : whileypath) {
			List<WyilFile> deps = new ArrayList<>();
			wyc.Compiler.extractDependencies(dep,deps);
			task.addSources(deps);
		}
		CLangFile target = task.run();
		// Write out binary target
		writeCLangFile(this.target, target, cdir);
		// Attempt to compile the source file.
		return compileCLangFile(this.target, cdir);
	}

	/**
	 * Command-line options
	 */
	private static final OptArg[] OPTIONS = {
			// Standard options
			new OptArg("verbose","v","set verbose output"),
			new OptArg("output","o",OptArg.STRING,"set output file","main"),
			new OptArg("wyildir", OptArg.FILEDIR, "Specify where to place binary (WyIL) files", new File(".")),
			new OptArg("jsdir", OptArg.FILEDIR, "Specify where to place JavaScript files", new File(".")),
			new OptArg("whileypath", OptArg.FILELIST, "Specify additional dependencies", new ArrayList<>())
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		//
		File wyildir = (File) options.get("wyildir");
		File jsdir = (File) options.get("jsdir");
		Trie target = Trie.fromString((String) options.get("output"));
		ArrayList<File> whileypath = (ArrayList<File>) options.get("whileypath");
		// Construct Main object
		Main main = new Main().setWyilDir(wyildir).setCDir(jsdir).setTarget(target).setWhileyPath(whileypath);
		// Add source files
		for (String s : args) {
			main.addSource(Trie.fromString(s));
		}
		// Run the compiler!
		boolean result = main.run();
		// Produce exit code
		System.exit(result ? 0 : 1);
	}

	/**
	 * Write a given WyilFile to disk using the given directory as a root.
	 *
	 * @param wf
	 * @param dir
	 * @throws IOException
	 */
	public static void writeCLangFile(Trie target, CLangFile wf, File dir) throws IOException {
		String filename = target.toNativeString() + ".c";
		try (FileOutputStream fout = new FileOutputStream(new File(dir, filename))) {
			new CLangFilePrinter(fout).write(wf);
			fout.flush();
		}
	}

	public static boolean compileCLangFile(Trie target, File dir) {
		File cfile = new File(dir, target.toNativeString() + ".c");
		File ofile = new File(dir, target.toNativeString());
		CC cc = new CC().setTarget(ofile);
		CC.Result r = cc.run(cfile);
		return r instanceof CC.Result.Success;
	}
}
