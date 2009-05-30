package com.horsefire.gwtamp.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import com.horsefire.gwtamp.client.GwtAmpRootPanel;
import com.horsefire.gwtamp.server.FileList;

public class HtmlGenerator {

	private final File m_outputDir;
	private final Log m_log;

	public HtmlGenerator(File outputDir, Log log) {
		m_outputDir = outputDir;
		m_log = log;
	}

	public void run(String[] gwtModules) throws MojoExecutionException,
			MojoFailureException {
		Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");

		writeFile(pattern, gwtModules[0], "index");
		for (int i = 1; i < gwtModules.length; i++) {
			writeFile(pattern, gwtModules[i], null);
		}
	}

	private void writeFile(Pattern pattern, String module, String filename)
			throws MojoFailureException, MojoExecutionException {
		String title = getLastName(module);
		if (filename == null) {
			filename = title;
		}

		BufferedReader reader = getReader();
		try {
			PrintWriter writer = new PrintWriter(new File(m_outputDir, filename
					+ ".html"));
			String line = reader.readLine();
			while (line != null) {
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					if ("title".equals(matcher.group(1))) {
						line = replaceMatch(line, matcher, title);
					} else if ("gwtModule".equals(matcher.group(1))) {
						line = replaceMatch(line, matcher, module);
					} else if ("rootPanelId".equals(matcher.group(1))) {
						line = replaceMatch(line, matcher, GwtAmpRootPanel.DIV_ID);
					} else {
						throw new MojoExecutionException(
								"Unknown variable in source index.html at line: "
										+ line);
					}

					matcher = pattern.matcher(line);
				}
				writer.println(line);
				line = reader.readLine();
			}
			writer.close();
		} catch (IOException e) {
			throw new MojoFailureException("Exception generating html files: "
					+ e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				m_log.info("Error closing stream: " + e);
			}
		}
	}

	private String replaceMatch(String source, Matcher matcher,
			String replacement) {
		return source.substring(0, matcher.start()) + replacement
				+ source.substring(matcher.end());
	}

	private String getLastName(String gwtModuleName) {
		String[] split = gwtModuleName.split("\\.");
		if (split.length > 1) {
			return split[split.length - 1];
		}
		return split[0];
	}

	private BufferedReader getReader() throws MojoExecutionException {
		InputStream stream = FileList.class.getResourceAsStream("index.html");
		if (stream == null) {
			throw new MojoExecutionException("Can't read index.html");
		}
		return new BufferedReader(new InputStreamReader(stream));
	}
}
