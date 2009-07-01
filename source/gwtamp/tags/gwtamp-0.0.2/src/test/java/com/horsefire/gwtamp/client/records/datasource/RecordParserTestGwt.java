package com.horsefire.gwtamp.client.records.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.AbstractGwtTestCase;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.values.BrokenLinkValue;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.LinkValue;

public class RecordParserTestGwt extends AbstractGwtTestCase {

	private static final String LINK_ONE_KEY = "linkOne";
	private static final String LINK_TWO_KEY = "linkTwo";

	private static final String DATA_ONE_KEY = "dataOne";
	private static final String DATA_TWO_KEY = "dataTwo";

	private LinkField m_link1;
	private LinkField m_link2;
	private List<LinkField> m_linkFields;

	private DataFieldStub m_data1;
	private DataValue m_realDataValue1;
	private DataValue m_defaultDataValue1;
	private DataFieldStub m_data2;
	private DataValue m_realDataValue2;
	private DataValue m_defaultDataValue2;
	private List<DataField> m_dataFields;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		m_link1 = new LinkField(LINK_ONE_KEY, "Title", null);
		m_link2 = new LinkField(LINK_TWO_KEY, "Title", null);

		m_realDataValue1 = new DataFieldStub.DataValueStub("real1");
		m_defaultDataValue1 = new DataFieldStub.DataValueStub("default1");
		m_data1 = new DataFieldStub(DATA_ONE_KEY, "Title", m_realDataValue1,
				m_defaultDataValue1);

		m_realDataValue2 = new DataFieldStub.DataValueStub("real2");
		m_defaultDataValue2 = new DataFieldStub.DataValueStub("default2");
		m_data2 = new DataFieldStub(DATA_TWO_KEY, "Title", m_realDataValue2,
				m_defaultDataValue2);

		m_dataFields = new ArrayList<DataField>();
		m_dataFields.add(m_data1);
		m_dataFields.add(m_data2);

		m_linkFields = new ArrayList<LinkField>();
		m_linkFields.add(m_link1);
		m_linkFields.add(m_link2);
	}

	public void testCreatingBrokenParser() {
		try {
			new RecordParser(null, m_dataFields, m_linkFields);
			fail("Should not be able to create a parser with no title field");
		} catch (IllegalArgumentException e) {
			// This is expected
		}
		try {
			new RecordParser("somethingelse", m_dataFields, m_linkFields);
			fail("Should not be able to create a parser with a title field that does not match any fields");
		} catch (IllegalArgumentException e) {
			// This is expected
		}
	}

	public void testFullRecordParsing() throws RecordParsingException {
		final RecordParser parser = new RecordParser(DATA_ONE_KEY,
				m_dataFields, m_linkFields);

		final String dataOneJson = "23.33";
		final String dataTwoJson = "\"Mr Jones\"";
		final int linkOneNumber = 4;
		final int linkTwoNumber = 82;

		JSONValue parsedValue = JSONParser.parse(createRecord(1, dataOneJson,
				dataTwoJson, Integer.toString(linkOneNumber), Integer
						.toString(linkTwoNumber)));

		Record record = parser.parseRecord(parsedValue.isObject());
		assertNotNull(record);
		assertEquals(1, record.getId());

		assertEquals(2, record.getDataKeys().size());
		DataValue dataValue = record.getDataValue(DATA_ONE_KEY);
		assertTrue(m_realDataValue1 == dataValue);
		assertEquals(dataOneJson, m_data1.providedJSON.toString());
		dataValue = record.getDataValue(DATA_TWO_KEY);
		assertTrue(m_realDataValue2 == dataValue);
		assertEquals(dataTwoJson, m_data2.providedJSON.toString());

		assertEquals(2, record.getLinkKeys().size());
		LinkValue linkValue = record.getLinkId(LINK_ONE_KEY);
		assertEquals(linkOneNumber, linkValue.getId());
		linkValue = record.getLinkId(LINK_TWO_KEY);
		assertEquals(linkTwoNumber, linkValue.getId());
	}

	public void testPartialRecordParsing() throws RecordParsingException {
		final RecordParser parser = new RecordParser(DATA_ONE_KEY,
				m_dataFields, m_linkFields);

		final String dataOneJson = "23.33";
		final int linkOneNumber = 4;

		JSONValue parsedValue = JSONParser.parse(createRecord(1, dataOneJson,
				null, Integer.toString(linkOneNumber), null));

		Record record = parser.parseRecord(parsedValue.isObject());
		assertNotNull(record);
		assertEquals(1, record.getId());

		assertEquals(2, record.getDataKeys().size());
		DataValue dataValue = record.getDataValue(DATA_ONE_KEY);
		assertTrue(m_realDataValue1 == dataValue);
		assertEquals(dataOneJson, m_data1.providedJSON.toString());
		dataValue = record.getDataValue(DATA_TWO_KEY);
		assertTrue(m_defaultDataValue2 == dataValue);
		assertNull(m_data2.providedJSON);

		assertEquals(2, record.getLinkKeys().size());
		LinkValue linkValue = record.getLinkId(LINK_ONE_KEY);
		assertEquals(linkOneNumber, linkValue.getId());
		linkValue = record.getLinkId(LINK_TWO_KEY);
		assertEquals(BrokenLinkValue.INSTANCE, linkValue);
	}

	private String createRecord(int id, String dataOne, String dataTwo,
			String linkOne, String linkTwo) {
		final StringBuilder builder = new StringBuilder().append("{").append(
				'"').append(Record.KEY_ID).append('"').append(':').append(id)
				.append(',').append('"').append(
						RecordParser.JSON_RECORD_KEY_DATA).append('"').append(
						":{");
		if (dataOne != null) {
			builder.append('"').append(DATA_ONE_KEY).append('"').append(':')
					.append(dataOne);
		}
		if (dataTwo != null) {
			if (dataOne != null) {
				builder.append(',');
			}
			builder.append('"').append(DATA_TWO_KEY).append('"').append(':')
					.append(dataTwo);
		}
		builder.append("},").append('"').append(
				RecordParser.JSON_RECORD_KEY_LINKS).append('"').append(":{");
		if (linkOne != null) {
			builder.append('"').append(LINK_ONE_KEY).append('"').append(':')
					.append(linkOne);
		}
		if (linkTwo != null) {
			if (linkOne != null) {
				builder.append(',');
			}
			builder.append('"').append(LINK_TWO_KEY).append('"').append(':')
					.append(linkTwo);
		}
		return builder.append("}}").toString();
	}
}
