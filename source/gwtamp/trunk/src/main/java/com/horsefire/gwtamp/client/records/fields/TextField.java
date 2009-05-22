package com.horsefire.gwtamp.client.records.fields;

/**
 * This class is just to tag string values to be rendered as a text area instead
 * of a text box
 */
public class TextField extends StringField {

	public TextField(String key, boolean userVisible, String title) {
		super(key, userVisible, title);
	}
}
