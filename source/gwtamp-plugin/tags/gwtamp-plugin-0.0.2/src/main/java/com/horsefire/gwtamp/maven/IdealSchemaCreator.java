package com.horsefire.gwtamp.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.IntegerField;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.client.records.fields.SwitchBundleField;
import com.horsefire.gwtamp.client.records.fields.TextField;
import com.horsefire.gwtamp.maven.DataSourceParser.DbLinkField;
import com.horsefire.gwtamp.maven.DataSourceParser.DbTable;

/**
 * Creates an sql file representing the ideal database schema for the current
 * DataSource setup. Saves to target/sql/[artifactId]-[version]-ideal.sql
 */
public class IdealSchemaCreator {

	public static final String VERSION_TABLE_NAME = "version";
	public static final String VERSION_TABLE_FIELD_VERSION = "version";
	public static final String VERSION_TABLE_FIELD_DATA_MODEL_INFO = "dataModelInfo";
	public static final String VERSION_TABLE_FIELD_NOTES = "notes";

	private static final String FILENAME_SUFFIX = "-ideal.sql";

	private final PrintWriter m_writer;
	private final DataSourceParser m_dataSourceParser;

	public IdealSchemaCreator(File targetDir, String artifactName,
			DataSource[] classes, String tablePrefix)
			throws MojoFailureException {
		final File outputFile = new File(targetDir, artifactName
				+ FILENAME_SUFFIX);
		try {
			m_writer = new PrintWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new MojoFailureException("Can't create sql file: " + e);
		}
		m_dataSourceParser = new DataSourceParser(classes, tablePrefix);
	}

	public void run() throws MojoExecutionException, MojoFailureException {
		List<DbTable> tables = m_dataSourceParser.getTables();

		for (DbTable table : tables) {
			m_writer.println("CREATE TABLE `" + table.dbName + "` (");
			m_writer.println("  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,");
			for (DataField field : table.dataFields) {
				m_writer.print("  `" + field.getKey() + "`");
				if (field instanceof IntegerField) {
					m_writer.print("INT UNSIGNED");
				} else if (field instanceof SwitchBundleField) {
					m_writer.print("INT UNSIGNED");
				} else if (field instanceof StringField) {
					if (field instanceof TextField) {
						m_writer.print("LONGTEXT");
					} else {
						StringField stringField = (StringField) field;
						m_writer.print("VARCHAR(" + stringField.getMaxLength()
								+ ")");
					}
				} else {
					throw new MojoExecutionException("Unknown data type: "
							+ field.getClass());
				}
				m_writer.println(",");
			}
			for (DbLinkField field : table.linkFields) {
				m_writer.println("  `" + field.name + "` INT UNSIGNED,");
			}
			for (DbLinkField field : table.linkFields) {
				m_writer.println("  FOREIGN KEY (`" + field.name
						+ "`) REFERENCES `" + field.linkedTable.dbName
						+ "`(`id`),");
			}
			m_writer.println("  PRIMARY KEY (`id`)");
			m_writer.println(") TYPE = INNODB;");
		}

		m_writer.println("CREATE TABLE `" + m_dataSourceParser.getTablePrefix()
				+ '_' + VERSION_TABLE_NAME + "` (");
		m_writer.println("  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,");
		m_writer.println("  `datestamp` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,");
		m_writer.println("  `" + VERSION_TABLE_FIELD_VERSION
				+ "` VARCHAR(20) NOT NULL,");
		m_writer.println("  `" + VERSION_TABLE_FIELD_DATA_MODEL_INFO
				+ "` TEXT NOT NULL,");
		m_writer.println("  `" + VERSION_TABLE_FIELD_NOTES
				+ "` LONGTEXT NOT NULL,");
		m_writer.println("  PRIMARY KEY (`id`)");
		m_writer.println(") TYPE = INNODB;");

		m_writer.close();
	}
}
