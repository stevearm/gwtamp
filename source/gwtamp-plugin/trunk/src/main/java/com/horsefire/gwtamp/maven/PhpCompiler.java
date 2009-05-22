package com.horsefire.gwtamp.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.NumberField;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.maven.DataSourceParser.DbLinkField;
import com.horsefire.gwtamp.maven.DataSourceParser.DbTable;

public class PhpCompiler {

	private final File m_outputDir;
	private final DataSourceParser m_dataSourceParser;

	public PhpCompiler(File outputDir, DataSource[] classes, String tablePrefix) {
		m_outputDir = new File(outputDir, DataSource.BASE_URL);
		m_outputDir.mkdir();

		m_dataSourceParser = new DataSourceParser(classes, tablePrefix);
	}

	public void run() throws MojoExecutionException, MojoFailureException {
		for (DbTable table : m_dataSourceParser.getTables()) {
			processTable(table);
		}
	}

	private void processTable(DbTable myClass) throws MojoExecutionException,
			MojoFailureException {
		Collection<String> stringDataFields = new ArrayList<String>();
		Collection<String> numberDataFields = new ArrayList<String>();
		for (DataField field : myClass.dataFields) {
			if (field instanceof NumberField) {
				numberDataFields.add(field.getKey());
			} else if (field instanceof StringField) {
				stringDataFields.add(field.getKey());
			} else {
				throw new MojoExecutionException("Unhandled datatype for key '"
						+ field.getKey() + "': " + field.getClass());
			}
		}
		Collection<String> linkFields = new ArrayList<String>();
		for (DbLinkField field : myClass.linkFields) {
			linkFields.add(field.name);
		}
		writeOutFile(myClass.name, myClass.dbName, stringDataFields,
				numberDataFields, linkFields);
	}

	private void writeOutFile(String fileName, String dbName,
			Collection<String> stringDataFields,
			Collection<String> numberDataFields, Collection<String> linkFields)
			throws MojoFailureException {
		try {
			final File outputFile = new File(m_outputDir, fileName + ".php");
			final PrintWriter writer = new PrintWriter(new FileWriter(
					outputFile));

			writer.println("<?php");
			writer.println("include '../includes/LocalConstants.php';");
			writer.println("include '../includes/DBManager.php';");
			writer.println("include '../includes/common.php';");

			writer.println("function getDatabaseName() { return '" + dbName
					+ "'; }");
			writer.println("function getStringDataFields() {");
			writer.println("  return array(" + renderFields(stringDataFields)
					+ ");");
			writer.println("}");
			writer.println("function getNumberDataFields() {");
			writer.println("  return array(" + renderFields(numberDataFields)
					+ ");");
			writer.println("}");
			writer.println("function getLinkFields() {");
			writer.println("  return array(" + renderFields(linkFields) + ");");
			writer.println("}");
			writer.println("?>");

			writer.close();
		} catch (IOException e) {
			throw new MojoFailureException("Error writing out file: " + e);
		}
	}

	private String renderFields(Collection<String> fields) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (String field : fields) {
			if (first) {
				first = false;
			} else {
				result.append(',');
			}
			result.append('\'').append(field).append('\'');
		}
		return result.toString();
	}
}
