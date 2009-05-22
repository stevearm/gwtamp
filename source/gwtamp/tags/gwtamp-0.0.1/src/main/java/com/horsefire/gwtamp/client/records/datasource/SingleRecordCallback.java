package com.horsefire.gwtamp.client.records.datasource;

import com.horsefire.gwtamp.client.records.Record;

public abstract class SingleRecordCallback extends Callback {

	public abstract void gotRecord(Record record);
}
