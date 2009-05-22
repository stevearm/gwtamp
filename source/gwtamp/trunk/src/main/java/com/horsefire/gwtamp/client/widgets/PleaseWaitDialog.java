package com.horsefire.gwtamp.client.widgets;

public interface PleaseWaitDialog {

	public void show(String message);

	public void hide();

	public boolean isShowing();

	public static class NullDialog implements PleaseWaitDialog {
		public void hide() {
		}

		public boolean isShowing() {
			return false;
		}

		public void show(String message) {
		}
	}
}
