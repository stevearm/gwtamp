package com.horsefire.gwtamp.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Concatenate all the src/main/sql/[artifactId]-[version].sql together,
 * concatenate the newly created target/sql/[artifactId]-[version].sql, and
 * write it to target/sql/[artifactId]-[version]-total.sql
 */
public class CumulativeSchemaCreator {

	private static final String SRC_SQL_DIR = "src/main/sql";

	private final File m_sourceDir;
	private final File m_outputDir;
	private final String m_artifactName;

	public CumulativeSchemaCreator(File workingDir, File outputDir,
			String artifactName) {
		m_sourceDir = new File(workingDir, SRC_SQL_DIR);
		m_outputDir = outputDir;
		m_artifactName = artifactName;
	}

	public void run() throws MojoFailureException {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(new File(m_outputDir,
					m_artifactName + "-total.sql")));
		} catch (IOException e) {
			throw new MojoFailureException(
					"Exception writing out total sql file: " + e);
		}

		final char[] buffer = new char[500];
		try {
			for (File oldFile : m_sourceDir.listFiles()) {
				if (oldFile.getName().endsWith("sql")) {
					FileReader reader = new FileReader(oldFile);
					int read = reader.read(buffer);
					while (read != -1) {
						writer.write(buffer, 0, read);
						read = reader.read(buffer);
					}
					writer.println();
				}
			}
		} catch (FileNotFoundException e) {
			throw new MojoFailureException("Can't find old sql file: " + e);
		} catch (IOException e) {
			throw new MojoFailureException("Exception reading sql file: " + e);
		}

		try {
			final FileReader reader = new FileReader(new File(m_outputDir,
					m_artifactName + ".sql"));
			int read = reader.read(buffer);
			while (read != -1) {
				writer.write(buffer, 0, read);
				read = reader.read(buffer);
			}
		} catch (FileNotFoundException e) {
			throw new MojoFailureException("Can't find new sql file: " + e);
		} catch (IOException e) {
			throw new MojoFailureException("Exception reading sql file: " + e);
		}

		writer.close();
	}
}
