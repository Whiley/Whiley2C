package wycl.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Provides a simple interface to the C compiler.
 *
 * @author David J. Pearce
 *
 */
public class CC {
	private final String ccCommand = "gcc";
	private File target = new File("a.out");

	public CC setTarget(File target) {
		this.target = target;
		return this;
	}

	public Result run(File... files) {
		ArrayList<String> args = new ArrayList<>();
		// String[] args = new String[files.length + 4];
		args.add(ccCommand);
		args.add("-o");
		args.add(target.toString());
		for (int i = 0; i != files.length; ++i) {
			args.add(files[i].toString());
		}
		try {
			// ===================================================
			// Construct the process
			// ===================================================
			ProcessBuilder builder = new ProcessBuilder(args);
			Process child = builder.start();
			try {
				// Second, read the result whilst checking for a timeout
				InputStream input = child.getInputStream();
				InputStream error = child.getErrorStream();
				int exitCode = child.waitFor();
				byte[] stdout = readInputStream(input);
				byte[] stderr = readInputStream(error);
				if (exitCode != 0) {
					return new Result.Error(exitCode, stdout, stderr);
				} else {
					return new Result.Success();
				}
			} finally {
				// make sure child process is destroyed.
				child.destroy();
			}
		} catch (IOException e) {
			return new Result.Failure(e);
		} catch (InterruptedException e) {
			return new Result.Failure(e);
		}
	}

	public interface Result {
		public static class Success implements Result {

		}

		/**
		 * Indicates the compiler reported some kind of error. Most likely this is an
		 * error in one of the files being compiled.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Error implements Result {
			private final int exitCode;
			private final byte[] stdout;
			private final byte[] stderr;

			public Error(int exitCode, byte[] stdout, byte[] stderr) {
				this.exitCode = exitCode;
				this.stdout = stdout;
				this.stderr = stderr;
			}

			public int getExitCode() {
				return exitCode;
			}

			public byte[] getStdOut() {
				return stdout;
			}
			public byte[] getStdErr() {
				return stderr;
			}
		}

		/**
		 * Indicates some kind of internal failure occurred trying to run the compiler.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Failure implements Result {
			private final Throwable exception;

			public Failure(Throwable exception) {
				this.exception = exception;
			}

			public Throwable getException() {
				return exception;
			}
		}
	}

    /**
     * Read an input stream entirely into a byte array.
     *
     * @param input
     * @return
     * @throws IOException
     */
    private static byte[] readInputStream(InputStream input) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (input.available() > 0) {
            int count = input.read(buffer);
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }
}
