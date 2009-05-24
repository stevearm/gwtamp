package com.horsefire.contactList.client;

import java.util.ArrayList;
import java.util.List;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;

public class PhoneNumberDataSource extends DataSource {

	public static final String KEY_DATA_TYPE = "type";
	public static final String KEY_DATA_NUMBER = "number";

	public static final String KEY_LINK_CONTACTID = "contactId";

	public static PhoneNumberDataSource create(ContactDataSource contactDS,
			PleaseWaitDialog dialog) {
		List<DataField> dataFields = new ArrayList<DataField>();
		dataFields.add(new StringField(KEY_DATA_TYPE, true,
				"Number type (home, cell)"));
		dataFields.add(new StringField(KEY_DATA_NUMBER, true, "Phone number",
				14));

		List<LinkField> linkFields = new ArrayList<LinkField>();
		linkFields.add(new LinkField(KEY_LINK_CONTACTID, "Contact", contactDS));

		return new PhoneNumberDataSource(dataFields, linkFields, dialog);
	}

	protected PhoneNumberDataSource(List<DataField> dataFields,
			List<LinkField> linkFields, PleaseWaitDialog waitDialog) {
		super("phonenumbers", dataFields, linkFields, KEY_DATA_NUMBER,
				waitDialog);
	}

}
