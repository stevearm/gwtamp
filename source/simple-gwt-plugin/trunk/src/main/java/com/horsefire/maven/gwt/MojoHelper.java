package com.horsefire.maven.gwt;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;

public class MojoHelper {

	public static void ensureOutputDir(File dir) throws MojoFailureException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new MojoFailureException("Can't create output dir: "
						+ dir);
			}
		}
	}
}
