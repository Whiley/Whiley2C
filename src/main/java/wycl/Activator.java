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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.Build.Project;
import wybs.lang.Build.Task;
import wybs.util.AbstractBuildRule;
import wybs.util.AbstractCompilationUnit.Value;
import wycc.cfg.Configuration;
import wycc.lang.Module;
import wycc.util.Logger;
import wycl.core.CLangFile;
import wycl.tasks.CLangCompileTask;
import wyfs.lang.Content;
import wyfs.lang.Content.Type;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.lang.Path.ID;
import wyfs.util.Trie;
import wyil.lang.WyilFile;

public class Activator implements Module.Activator {

	private static Trie PKGNAME_CONFIG_OPTION = Trie.fromString("package/name");
	private static Trie SOURCE_CONFIG_OPTION = Trie.fromString("build/whiley/target");
	private static Trie TARGET_CONFIG_OPTION = Trie.fromString("build/jctarget");
	private static Value.UTF8 TARGET_DEFAULT = new Value.UTF8("bin/c".getBytes());


	// =======================================================================
	// Build Platform
	// =======================================================================

	public static Build.Platform C_PLATFORM = new Build.Platform() {

		@Override
		public String getName() {
			return "c";
		}

		@Override
		public Configuration.Schema getConfigurationSchema() {
			return Configuration.fromArray(Configuration.UNBOUND_STRING(TARGET_CONFIG_OPTION,
					"Specify location for generated JavaScript files", TARGET_DEFAULT));
		}

		@Override
		public void initialise(Configuration configuration, Build.Project project) throws IOException {
			Trie pkgName = Trie.fromString(configuration.get(Value.UTF8.class, PKGNAME_CONFIG_OPTION).unwrap());
			// Specify directory where generated JS files are dumped.
			Trie source = Trie.fromString(configuration.get(Value.UTF8.class, SOURCE_CONFIG_OPTION).unwrap());
			// Construct the source root
			Path.Root sourceRoot = project.getRoot().createRelativeRoot(source);
			// Register build target for this package
			registerBuildTarget(configuration,project,sourceRoot,pkgName);
		}

		@Override
		public Type<?> getSourceType() {
			return WyilFile.ContentType;
		}

		@Override
		public Type<?> getTargetType() {
			return CLangFile.ContentType;
		}

		@Override
		public void execute(Project project, ID path, String name, Value... args) {
			throw new IllegalArgumentException("native JavaScript execution currently unsupported");
		}

		private void registerBuildTarget(Configuration configuration, Build.Project project, Path.Root sourceRoot,
				Trie pkg) throws IOException {
			// Specify directory where generated JS files are dumped.
			Trie target= Trie.fromString(configuration.get(Value.UTF8.class, TARGET_CONFIG_OPTION).unwrap());
			// Specify set of files included
			Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
			// Construct the binary root
			Path.Root binaryRoot = project.getRoot().createRelativeRoot(target);
			// Initialise the target file being built
			Path.Entry<CLangFile> binary = initialiseBinaryTarget(binaryRoot, pkg);
			//
			project.getRules().add(new AbstractBuildRule<WyilFile, CLangFile>(sourceRoot, wyilIncludes, null) {

				@Override
				protected void apply(List<Entry<WyilFile>> matches, Collection<Task> tasks) throws IOException {
					CLangCompileTask task = new CLangCompileTask(project, binary, matches.get(0));
					tasks.add(task);
				}

			});
		}

		private Path.Entry<CLangFile> initialiseBinaryTarget(Path.Root binroot, Path.ID id) throws IOException {
			Path.Entry<CLangFile> target;
			if (binroot.exists(id, CLangFile.ContentType)) {
				// Yes, it does so reuse it.
				target = binroot.get(id, CLangFile.ContentType);
			} else {
				// No, it doesn't so create and initialise it
				target = binroot.create(id, CLangFile.ContentType);
			}
			// Initialise with empty JavaScript file
			CLangFile jsf = new CLangFile();
			// Write
			target.write(jsf);
			// Done
			return target;
		}
	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Module start(Module.Context context) {
		// FIXME: logger is a hack!
		final Logger logger = new Logger.Default(System.err);
		// Register build platform
		context.register(Build.Platform.class, C_PLATFORM);
		// Register JavaScript Content Type
		context.register(Content.Type.class, CLangFile.ContentType);
		// Done
		return new Module() {
			// what goes here?
		};
	}

	// =======================================================================
	// Stop
	// =======================================================================

	@Override
	public void stop(Module module, Module.Context context) {
		// could do more here?
	}
}
