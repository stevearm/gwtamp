package com.horsefire.gwtamp.client.records.datasource;

public interface DataSourceBundle {

	/**
	 * This array must return DataSources in a dependent order such that if
	 * DataSource A has a link field to DataSource B, A must be after B in the
	 * array.
	 * 
	 * @return an array of all DataSource classes in this project
	 */
	DataSource[] getDataSources();
}
