package com.horsefire.gwtamp.maven;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;

public class DataSourceParser {

	public static class DbLinkField {
		public String name;
		public DbTable linkedTable;
	}

	public static class DbTable {
		public String name;
		public String dbName;
		public List<DataField> dataFields;
		public List<DbLinkField> linkFields;
	}

	private final DataSource[] m_classes;
	private final List<DbTable> m_tables;
	private final Map<DataSource, DbTable> m_tableHash;
	private final String m_tablePrefix;

	public DataSourceParser(DataSource[] classes, String tablePrefix) {
		m_classes = classes;
		m_tables = new ArrayList<DbTable>();
		m_tableHash = new HashMap<DataSource, DbTable>();
		m_tablePrefix = tablePrefix;
	}

	public String getTablePrefix() {
		return m_tablePrefix;
	}

	public List<DbTable> getTables() throws MojoExecutionException,
			MojoFailureException {
		if (m_tables.isEmpty()) {
			parseClasses();
		}
		return m_tables;
	}

	private void parseClasses() throws MojoExecutionException,
			MojoFailureException {
		try {
			final Class<?> dataSourceClass = DataSource.class;
			Field dataListField = dataSourceClass
					.getDeclaredField("m_dataFields");
			dataListField.setAccessible(true);
			Field linkListField = dataSourceClass
					.getDeclaredField("m_linkFields");
			linkListField.setAccessible(true);

			for (DataSource dataSource : m_classes) {
				DbTable table = parseDataSource(dataSource, 
						dataListField, linkListField);
				m_tables.add(table);
				m_tableHash.put(dataSource, table);
			}
		} catch (SecurityException e) {
			throw new MojoExecutionException("Can't parse DataSource classes: "
					+ e);
		} catch (NoSuchFieldException e) {
			throw new MojoExecutionException(
					"Can't find the right fields in DataSource: " + e);
		} catch (IllegalArgumentException e) {
			throw new MojoExecutionException("Can't parse DataSource classes: "
					+ e);
		} catch (IllegalAccessException e) {
			throw new MojoExecutionException("Can't parse DataSource classes: "
					+ e);
		}
	}

	@SuppressWarnings("unchecked")
	private DbTable parseDataSource(DataSource dataSource, 
			Field dataListField, Field linkListField)
			throws IllegalArgumentException, IllegalAccessException,
			MojoFailureException {
		DbTable table = new DbTable();
		table.name = dataSource.getName();
		table.dbName = m_tablePrefix + '_' + table.name;
		table.dataFields = (List<DataField>) dataListField.get(dataSource);
		table.linkFields = new ArrayList<DbLinkField>();
		for (LinkField linkField : (List<LinkField>) linkListField
				.get(dataSource)) {
			DbLinkField dbLinkField = new DbLinkField();
			dbLinkField.name = linkField.getKey();
			dbLinkField.linkedTable = m_tableHash
					.get(linkField.getDataSource());
			if (dbLinkField.linkedTable == null) {
				throw new MojoFailureException("Link field '"
						+ dbLinkField.name + "' links to a null table: "
						+ linkField.getDataSource().getClass());
			}
			table.linkFields.add(dbLinkField);
		}
		return table;
	}
}
