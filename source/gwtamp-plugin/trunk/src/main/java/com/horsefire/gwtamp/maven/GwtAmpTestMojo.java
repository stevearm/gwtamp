package com.horsefire.gwtamp.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.DataSourceBundle;
import com.horsefire.gwtamp.client.records.fields.Field;

/**
 * @goal test
 * @requiresDependencyResolution runtime
 */
public class GwtAmpTestMojo extends AbstractMojo {

	/**
	 * Main GWT module
	 * 
	 * @parameter
	 * @required
	 */
	private String gwtModule;

	/**
	 * Gwt test suite
	 * 
	 * @parameter
	 * @required
	 */
	private String gwtTestSuite;

	/**
	 * Gwt version. Just use a single property for this and your dependency
	 * 
	 * @parameter expression="${gwtHome}/gwt-${osName}-${gwt.version}"
	 */
	private File gwtHome;

	/**
	 * String for os: linux, mac, or windows
	 * 
	 * @parameter expression="${osName}"
	 */
	private String osName;

	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${basedir}"
	 * @required
	 * @readonly
	 */
	private File baseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (extraGwtModules == null) {
			extraGwtModules = new String[0];
			return;
		}

		ensureOutputDir(artifactOutputDir);
		ensureOutputDir(sqlOutputDir);

		final String[] allGwtModules = new String[extraGwtModules.length + 1];
		allGwtModules[0] = gwtModule;
		for (int i = 0; i < extraGwtModules.length; i++) {
			allGwtModules[i + 1] = extraGwtModules[i];
		}
		runGwtCompile(allGwtModules);
		new HtmlGenerator(artifactOutputDir, getLog()).run(allGwtModules);
		new ServerSideFileCopier(artifactOutputDir).run();

		DataSource[] dataSources = getDataSources();

		verifyDataSourceUniqueness(dataSources);

		new PhpCompiler(artifactOutputDir, dataSources, databaseTablePrefix)
				.run();

		new IdealSchemaCreator(sqlOutputDir, artifactName, dataSources,
				databaseTablePrefix).run();
		new IncrementalSchemaCreator(projectVersion, baseDir, sqlOutputDir,
				artifactName, dataSources, databaseTablePrefix).run();
		new CumulativeSchemaCreator(baseDir, sqlOutputDir, artifactName).run();
	}

	public static void ensureOutputDir(File dir) throws MojoFailureException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new MojoFailureException("Can't create output dir: "
						+ dir);
			}
		}
	}

	private void runGwtCompile(String[] modules) throws MojoExecutionException {
		if (!gwtHome.isDirectory()) {
			throw new MojoExecutionException(
					"Could not find GWT installed at: "
							+ gwtHome.getAbsolutePath());
		}
		final String command = getGwtClasspath()
				+ " com.google.gwt.dev.GWTCompiler -out "
				+ artifactOutputDir.getAbsolutePath() + ' ';
		final Runtime runtime = Runtime.getRuntime();
		for (String module : modules) {
			try {
				final Process exec = runtime.exec(command + module);
				final BufferedReader input = new BufferedReader(
						new InputStreamReader(exec.getInputStream()));
				String line = null;
				while ((line = input.readLine()) != null) {
					getLog().info("GWT-compile> " + line);
				}
				final int exitCode = exec.waitFor();
				if (exitCode != 0) {
					throw new MojoExecutionException(
							"GWT compile failed with exit code " + exitCode);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Exception running GWT compile command: '" + command
								+ module + "'", e);
			} catch (InterruptedException e) {
				throw new MojoExecutionException(
						"Exception running GWT compile command: '" + command
								+ module + "'", e);
			}
		}
	}

	private String getGwtClasspath() throws MojoExecutionException {
		final StringBuilder classpath = new StringBuilder(
				"java -Xmx512m -classpath src/main/java")
				.append(File.pathSeparatorChar);
		classpath.append(new File(gwtHome, "gwt-user.jar").getAbsolutePath())
				.append(File.pathSeparatorChar);
		classpath.append(
				new File(gwtHome, "gwt-dev-" + osName + ".jar")
						.getAbsolutePath()).append(File.pathSeparatorChar);
		for (String element : getRuntimeClasspathElements()) {
			classpath.append(element).append(File.pathSeparatorChar);
		}
		classpath.deleteCharAt(classpath.length() - 1);
		return classpath.toString();
	}

	@SuppressWarnings("unchecked")
	private List<String> getRuntimeClasspathElements()
			throws MojoExecutionException {
		try {
			return project.getRuntimeClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException(
					"Exception querying for runtime classpath elements: " + e);
		}
	}

	@SuppressWarnings("unchecked")
	private DataSource[] getDataSources() throws MojoExecutionException,
			MojoFailureException {
		final List<URL> runtimeUrls = new ArrayList<URL>();
		for (String element : getRuntimeClasspathElements()) {
			try {
				runtimeUrls.add(new File(element).toURI().toURL());
			} catch (MalformedURLException e) {
				throw new MojoExecutionException(
						"Exception creating classloader URLs: " + e);
			}
		}
		final URL[] urls = runtimeUrls.toArray(new URL[runtimeUrls.size()]);
		final URLClassLoader newLoader = new URLClassLoader(urls, Thread
				.currentThread().getContextClassLoader());

		Class dataSourceBundleClass;
		try {
			dataSourceBundleClass = newLoader.loadClass(dataSourceBundle);
		} catch (ClassNotFoundException e) {
			throw new MojoFailureException("Class not found: " + e);
		}

		Constructor ctor;
		try {
			ctor = dataSourceBundleClass.getConstructor(new Class[] {});
		} catch (SecurityException e) {
			throw new MojoFailureException(e.toString());
		} catch (NoSuchMethodException e) {
			throw new MojoFailureException(
					"Configured DataSourceBundle doesn't have a no-arguement constructor");
		}
		DataSourceBundle bundle;
		try {
			bundle = (DataSourceBundle) ctor.newInstance(new Object[] {});
		} catch (IllegalArgumentException e) {
			throw new MojoFailureException(e.toString());
		} catch (InstantiationException e) {
			throw new MojoFailureException(e.toString());
		} catch (IllegalAccessException e) {
			throw new MojoFailureException(e.toString());
		} catch (InvocationTargetException e) {
			throw new MojoFailureException(e.toString());
		}

		return bundle.getDataSources();
	}

	private void verifyDataSourceUniqueness(DataSource[] dataSources)
			throws MojoFailureException {
		final Set<String> dsNames = new HashSet<String>();
		for (DataSource ds : dataSources) {
			if (!dsNames.add(ds.getName())) {
				throw new MojoFailureException(
						"Duplicate name found for DataSources: " + ds.getName());
			}
			final Set<String> fieldNames = new HashSet<String>();
			for (Field field : ds.getDataFields()) {
				if (!fieldNames.add(field.getKey())) {
					throw new MojoFailureException(
							"Duplicate field name found in " + ds.getClass()
									+ ": " + field.getKey());
				}
			}
			for (Field field : ds.getLinkFields()) {
				if (!fieldNames.add(field.getKey())) {
					throw new MojoFailureException(
							"Duplicate field name found in " + ds.getClass()
									+ ": " + field.getKey());
				}
			}
		}
	}
}
