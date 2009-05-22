package com.horsefire.gwtamp.client.records.datasource;

import java.util.Collection;

import com.horsefire.gwtamp.client.records.Record;

public abstract class MultipleRecordCallback extends Callback {

	public abstract void gotRecords(Collection<Record> records);
}
