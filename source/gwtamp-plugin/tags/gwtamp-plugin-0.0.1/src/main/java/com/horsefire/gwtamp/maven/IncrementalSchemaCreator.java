package com.horsefire.gwtamp.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.SwitchBundleField;
import com.horsefire.gwtamp.maven.DataSourceParser.DbTable;

/**
 * Creates a [artifactId]-[version].sql incremental file using current
 * DataSource settings, whatever is written in
 * src/main/sql/current/releaseNotes.txt and in src/main/sql/schemaChanges.sql.
 */
public class IncrementalSchemaCreator {

	private static final String SCHEMA_CHANGES_FILENAME = "src/main/sql/currentVersion/schemaChanges.sql";
	private static final String RELEASE_NOTES_FILENAME = "src/main/sql/currentVersion/releaseNotes.txt";

	private final String m_productVersion;
	private final PrintWriter m_writer;
	private final FileReader m_schemaChangesReader;
	private final FileReader m_releaseNotesReader;
	private final DataSourceParser m_dataSourceParser;

	public IncrementalSchemaCreator(String productVersion, File workingDir,
			File sqlOutputDir, String artifactName, DataSource[] dataSources,
			String tablePrefix) throws MojoFailureException,
			MojoExecutionException {
		m_productVersion = productVersion;
		m_dataSourceParser = new DataSourceParser(dataSources, tablePrefix);

		try {
			m_writer = new PrintWriter(new FileWriter(new File(sqlOutputDir,
					artifactName + ".sql")));
		} catch (IOException e) {
			throw new MojoFailureException(
					"Error writing out incremental sql file: " + e);
		}

		final File schemaChangesFile = new File(workingDir,
				SCHEMA_CHANGES_FILENAME);
		if (schemaChangesFile.isFile()) {
			try {
				m_schemaChangesReader = new FileReader(schemaChangesFile);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException("No schema changes file: " + e);
			}
		} else {
			throw new MojoExecutionException("No schema changes file");
		}

		final File releaseNotesFile = new File(workingDir,
				RELEASE_NOTES_FILENAME);
		if (releaseNotesFile.isFile()) {
			try {
				m_releaseNotesReader = new FileReader(releaseNotesFile);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException("No release notes file: " + e);
			}
		} else {
			throw new MojoExecutionException("No release notes file");
		}
	}

	public void run() throws MojoFailureException, MojoExecutionException {
		m_writer
				.println("-- Start of version " + m_productVersion + " section");
		if (m_schemaChangesReader != null) {
			final char[] buffer = new char[500];
			int read;
			try {
				read = m_schemaChangesReader.read(buffer);
				while (read != -1) {
					m_writer.write(buffer, 0, read);
					read = m_schemaChangesReader.read(buffer);
				}
			} catch (IOException e) {
				throw new MojoFailureException("Error reading schema changes: "
						+ e);
			}
		}
		m_writer.println();
		m_writer.print("INSERT INTO `" + IdealSchemaCreator.VERSION_TABLE_NAME
				+ "`(`" + IdealSchemaCreator.VERSION_TABLE_FIELD_VERSION
				+ "`, `"
				+ IdealSchemaCreator.VERSION_TABLE_FIELD_DATA_MODEL_INFO
				+ "`, `" + IdealSchemaCreator.VERSION_TABLE_FIELD_NOTES
				+ "`) VALUES ('" + m_productVersion + "', '"
				+ renderDataModelInfo() + "', '");
		if (m_releaseNotesReader != null) {
			final char newLine = '\n';
			final char carraigeReturn = '\r';
			final char singleQuote = '\'';
			int read;
			try {
				read = m_releaseNotesReader.read();
				while (read != -1) {
					final char character = (char) read;
					switch (character) {
					case newLine:
						m_writer.write("\\n");
						break;
					case carraigeReturn:
					case singleQuote:
						// Do nothing
						break;
					default:
						m_writer.write(read);
					}
					read = m_releaseNotesReader.read();
				}
			} catch (IOException e) {
				throw new MojoFailureException("Error reading release notes: "
						+ e);
			}
		}
		m_writer.println("');");
		m_writer.println("-- End of version " + m_productVersion + " section");
		m_writer.close();
	}

	private String renderDataModelInfo() throws MojoExecutionException,
			MojoFailureException {
		StringBuilder result = new StringBuilder('{');
		renderSwitches(result);
		return result.append('}').toString();
	}

	private void renderSwitches(StringBuilder result)
			throws MojoExecutionException, MojoFailureException {
		result.append("\"switches\":{");
		boolean firstTable = true;
		for (DbTable table : m_dataSourceParser.getTables()) {
			SwitchBundleField switchBundleField = null;
			for (DataField dataField : table.dataFields) {
				if (dataField instanceof SwitchBundleField) {
					switchBundleField = (SwitchBundleField) dataField;
				}
			}
			if (switchBundleField != null) {
				if (firstTable) {
					firstTable = false;
				} else {
					result.append(',');
				}
				result.append('"').append(table.dbName).append("\":[");
				boolean firstKey = true;
				for (String key : switchBundleField.getKeys()) {
					if (firstKey) {
						firstKey = false;
					} else {
						result.append(',');
					}
					result.append('"').append(key).append('"');
				}
				result.append(']');
			}
		}
		result.append('}');
	}
}
