package com.horsefire.gwtamp.client.records.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.SingleRecordCallback;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.DateField;
import com.horsefire.gwtamp.client.records.fields.IntegerField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.client.records.fields.SwitchBundleField;
import com.horsefire.gwtamp.client.records.fields.TextField;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.DateValue;
import com.horsefire.gwtamp.client.records.values.IntegerValue;
import com.horsefire.gwtamp.client.records.values.LinkValue;
import com.horsefire.gwtamp.client.records.values.StringValue;
import com.horsefire.gwtamp.client.records.values.SwitchBundleValue;

public class EditForm extends Composite {

	private final DataSource m_dataSource;
	private final VerticalPanel m_mainTable;
	private final Widget m_buttonPanel;
	private final Button m_saveButton;

	private final List<DataField> m_dataFields;
	private final List<LinkField> m_linkFields;

	private Record m_currentRecord;
	private List<Change> m_newRecordPresets;

	public EditForm(DataSource dataSource, boolean showButtons) {
		this(dataSource, showButtons, null, null);
	}

	/**
	 * If the Lists passed in are null, display all fields. If not, only display
	 * the fields in the Lists
	 */
	public EditForm(DataSource dataSource, boolean showButtons,
			List<DataField> dataFieldsToShow, List<LinkField> linkFieldsToShow) {
		m_dataSource = dataSource;

		if (dataFieldsToShow == null) {
			m_dataFields = m_dataSource.getDataFields();
		} else {
			m_dataFields = dataFieldsToShow;
		}

		if (linkFieldsToShow == null) {
			m_linkFields = m_dataSource.getLinkFields();
		} else {
			m_linkFields = linkFieldsToShow;
		}

		m_saveButton = new Button("Save", new ClickListener() {
			public void onClick(Widget sender) {
				save(new SingleRecordCallback() {
					public void gotRecord(Record record) {
					}
				});
			}
		});

		m_mainTable = new VerticalPanel();
		initWidget(m_mainTable);

		if (showButtons) {
			m_buttonPanel = createButtonPanel();
		} else {
			m_buttonPanel = new HorizontalPanel();
		}
	}

	public void addPresetChange(List<Change> newRecordPresets) {
		m_newRecordPresets = newRecordPresets;
	}

	private Widget createButtonPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(m_saveButton);
		return panel;
	}

	public void setRecord(Record record) {
		setRecord(record, null);
	}

	public void setRecord(Record record, List<Change> newRecordPresets) {
		m_currentRecord = record;
		m_newRecordPresets = newRecordPresets;
		reset();
	}

	public Record getRecord() {
		return m_currentRecord;
	}

	public void reset() {
		m_mainTable.clear();
		m_mainTable.add(createWidget(m_currentRecord));
		m_mainTable.add(m_buttonPanel);
	}

	private Widget createWidget(Record record) {
		FlexTable table = new FlexTable();

		int i = 0;
		for (DataField field : m_dataFields) {
			table.setWidget(i, 0, new Label(field.getTitle()));
			DataValue value = null;
			if (record != null) {
				value = record.getDataValue(field.getKey());
			}
			EditField editField = renderField(field, value);
			table.setWidget(i, 1, editField);
			i++;
		}
		for (LinkField field : m_linkFields) {
			table.setWidget(i, 0, new Label(field.getTitle()));
			LinkValue value = null;
			if (record != null) {
				value = record.getLinkId(field.getKey());
			}
			EditField editField = renderField(field, value);
			table.setWidget(i, 1, editField);
			i++;
		}
		return table;
	}

	protected EditField renderField(DataField field, DataValue value) {
		if (field instanceof StringField) {
			if (field instanceof TextField) {
				return new TextEditField(field.getKey(), (StringValue) value);
			}
			return new StringEditField((StringField) field, (StringValue) value);
		} else if (field instanceof IntegerField) {
			if (field instanceof DateField) {
				return new DateEditField(field.getKey(), (DateValue) value);
			}
			return new IntegerEditField(field.getKey(), (IntegerValue) value);
		} else if (field instanceof SwitchBundleField) {
			return new SwitchBundleEditField((SwitchBundleField) field,
					(SwitchBundleValue) value);
		}
		return new BlankEditField(field.getKey(), "Unknown data type");
	}

	protected EditField renderField(final LinkField field, final LinkValue value) {
		return new RecordSelectorField(field, value);
	}

	public void save(final SingleRecordCallback callback) {
		Collection<Change> changes = getChanges();
		if (m_newRecordPresets != null) {
			changes.addAll(m_newRecordPresets);
		}

		if (m_currentRecord == null) {
			m_dataSource.addRecord(changes, callback);
		} else {
			m_dataSource.saveRecord(m_currentRecord, changes, callback);
		}
	}

	protected Collection<Change> getChanges() {
		Collection<Change> changes = new ArrayList<Change>();
		if (m_newRecordPresets != null && !m_newRecordPresets.isEmpty()) {
			changes.addAll(m_newRecordPresets);
		}
		FlexTable table = (FlexTable) m_mainTable.getWidget(0);
		for (int i = 0; i < table.getRowCount(); i++) {
			EditField editField = (EditField) table.getWidget(i, 1);
			Change change = editField.getChange();
			if (change != null) {
				changes.add(change);
			}
		}
		return changes;
	}
}
