package com.horsefire.gwtamp.maven;

import java.io.File;
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
 * @goal package
 * @requiresDependencyResolution runtime
 */
public class GwtAmpMojo extends AbstractMojo {

	/**
	 * Main GWT module
	 * 
	 * @parameter
	 * @required
	 */
	private String gwtModule;

	/**
	 * Class that implements DataSourceBundle
	 * 
	 * @parameter
	 * @required
	 */
	private String dataSourceBundle;

	/**
	 * Array of extra GWT module
	 * 
	 * @parameter
	 */
	private String[] extraGwtModules;

	/**
	 * Database table prefix
	 * 
	 * @parameter expression="${project.artifactId}"
	 */
	private String databaseTablePrefix;

	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Project version
	 * 
	 * @parameter expression="${project.version}"
	 * @required
	 * @readonly
	 */
	private String projectVersion;

	/**
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 * @readonly
	 */
	private String artifactName;

	/**
	 * @parameter expression="${basedir}/target/${project.build.finalName}"
	 * @required
	 * @readonly
	 */
	private File artifactOutputDir;

	/**
	 * @parameter expression="${basedir}/target/sql"
	 * @required
	 * @readonly
	 */
	private File sqlOutputDir;

	/**
	 * @parameter expression="${basedir}"
	 * @required
	 * @readonly
	 */
	private File baseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (extraGwtModules == null) {
			extraGwtModules = new String[0];
		}

		ensureOutputDir(artifactOutputDir);
		ensureOutputDir(sqlOutputDir);

		final String[] allGwtModules = new String[extraGwtModules.length + 1];
		allGwtModules[0] = gwtModule;
		for (int i = 0; i < extraGwtModules.length; i++) {
			allGwtModules[i + 1] = extraGwtModules[i];
		}
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
