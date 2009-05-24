package com.horsefire.contactList.client;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;

public class DataSourceBundle implements
		com.horsefire.gwtamp.client.records.datasource.DataSourceBundle {

	private final ContactDataSource m_contactDS;
	private final PhoneNumberDataSource m_phoneNumberDS;

	public DataSourceBundle() {
		this(new PleaseWaitDialog.NullDialog());
	}

	public DataSourceBundle(PleaseWaitDialog dialog) {
		m_contactDS = ContactDataSource.create(dialog);
		m_phoneNumberDS = PhoneNumberDataSource.create(m_contactDS, dialog);
	}

	public DataSource[] getDataSources() {
		return new DataSource[] { m_contactDS, m_phoneNumberDS };
	}

	public ContactDataSource getContactDS() {
		return m_contactDS;
	}

	public PhoneNumberDataSource getPhoneNumberDS() {
		return m_phoneNumberDS;
	}
}
