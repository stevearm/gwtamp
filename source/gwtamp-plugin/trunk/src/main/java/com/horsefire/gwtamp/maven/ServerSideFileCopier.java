package com.horsefire.gwtamp.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.horsefire.gwtamp.server.FileList;

public class ServerSideFileCopier {

	private final File m_outputDir;

	public ServerSideFileCopier(File outputDir) {
		m_outputDir = outputDir;
	}

	public void run() throws MojoExecutionException, MojoFailureException {
		for (String dir : FileList.DIRS) {
			GwtAmpMojo.ensureOutputDir(new File(m_outputDir, dir));
		}

		byte[] buffer = new byte[200];
		int read = 0;
		try {
			for (String filename : FileList.FILES) {
				InputStream stream = FileList.class
						.getResourceAsStream(filename);
				if (stream == null) {
					throw new MojoExecutionException("Can't read file '"
							+ filename + "'");
				}
				FileOutputStream output = new FileOutputStream(new File(
						m_outputDir, filename));
				read = stream.read(buffer);
				while (read > 0) {
					output.write(buffer, 0, read);
					read = stream.read(buffer);
				}
			}
		} catch (IOException e) {
			throw new MojoFailureException("Error copying file: " + e);
		}
	}
}
