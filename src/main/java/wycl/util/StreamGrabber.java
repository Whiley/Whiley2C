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
package wycl.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Grab everything produced by a given input stream until the End-Of-File (EOF)
 * is reached. This is implemented as a separate thread to ensure that reading
 * from other streams can happen concurrently. For example, we can read
 * concurrently from <code>stdin</code> and <code>stderr</code> for some process
 * without blocking that process.
 *
 * @author David J. Pearce
 *
 */
public class StreamGrabber extends Thread {
	private InputStream input;
	private StringBuffer buffer;

	public StreamGrabber(InputStream input, StringBuffer buffer) {
		this.input = input;
		this.buffer = buffer;
		start();
	}

	@Override
	public void run() {
		try {
			int nextChar;
			// keep reading!!
			while ((nextChar = input.read()) != -1) {
				buffer.append((char) nextChar);
			}
		} catch (IOException ioe) {
		}
	}
}