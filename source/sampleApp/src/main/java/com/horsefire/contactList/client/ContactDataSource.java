package com.horsefire.contactList.client;

import java.util.ArrayList;
import java.util.List;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.DateField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.client.rpc.RpcClient;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;

public class ContactDataSource extends DataSource {

	public static final String KEY_DATA_NAME = "name";
	public static final String KEY_DATA_BIRTHDAY = "birthDay";

	public static ContactDataSource create(PleaseWaitDialog dialog) {
		List<DataField> dataFields = new ArrayList<DataField>();
		dataFields.add(new StringField(KEY_DATA_NAME, "Name", 50));
		dataFields.add(new DateField(KEY_DATA_BIRTHDAY, "Birthdate"));

		return new ContactDataSource(dataFields, dialog);
	}

	protected ContactDataSource(List<DataField> dataFields,
			PleaseWaitDialog waitDialog) {
		super("contacts", dataFields, new ArrayList<LinkField>(),
				KEY_DATA_NAME, waitDialog, new RpcClient());
	}
}
