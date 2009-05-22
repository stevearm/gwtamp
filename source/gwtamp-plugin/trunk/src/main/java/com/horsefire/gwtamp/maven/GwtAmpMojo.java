package com.horsefire.gwtamp.maven;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.DataSourceBundle;

/**
 * @goal doEverything
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
	 * Array of extra GWT module
	 * 
	 * @parameter
	 */
	private String[] extraGwtModules;

	/**
	 * Class that implements DataSourceBundle
	 * 
	 * @parameter
	 * @required
	 */
	private String dataSourceBundle;

	/**
	 * Database table prefix
	 * 
	 * @parameter
	 * @required
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
	 * The Maven Session Object
	 * 
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	private MavenSession session;

	/**
	 * Project version
	 * 
	 * @parameter expression="${project.version}"
	 * @required
	 * @readonly
	 */
	private String projectVersion;

	/**
	 * The Maven PluginManager Object
	 * 
	 * @component
	 * @required
	 */
	private PluginManager pluginManager;

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
		runGwtCompilePlugin(allGwtModules);
		new HtmlGenerator(artifactOutputDir, getLog()).run(allGwtModules);
		new ServerSideFileCopier(artifactOutputDir).run();

		DataSource[] dataSources = getDataSources();

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

	private void runGwtCompilePlugin(String[] modules)
			throws MojoExecutionException {
		Plugin gwtPlugin = MojoExecutor.plugin("org.codehaus.mojo",
				"gwt-maven-plugin", "1.0");
		ExecutionEnvironment executionEnvironment = MojoExecutor
				.executionEnvironment(project, session, pluginManager);
		for (String module : modules) {
			MojoExecutor.executeMojo(gwtPlugin, "compile", MojoExecutor
					.configuration(MojoExecutor.element("module", module)),
					executionEnvironment);
		}
	}

	private DataSource[] getDataSources() throws MojoExecutionException,
			MojoFailureException {
		List runtimeClasspathElements;
		try {
			runtimeClasspathElements = project.getRuntimeClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException(
					"Exception querying for runtime classpath elements: " + e);
		}
		URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
		for (int i = 0; i < runtimeClasspathElements.size(); i++) {
			String element = (String) runtimeClasspathElements.get(i);
			try {
				runtimeUrls[i] = new File(element).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new MojoExecutionException(
						"Exception creating classloader URLs: " + e);
			}
		}
		URLClassLoader newLoader = new URLClassLoader(runtimeUrls, Thread
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
}
