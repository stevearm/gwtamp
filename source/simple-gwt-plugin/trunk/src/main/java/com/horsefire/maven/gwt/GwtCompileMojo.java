package com.horsefire.maven.gwt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal compile
 * @requiresDependencyResolution runtime
 */
public class GwtCompileMojo extends AbstractMojo {

	/**
	 * Array of GWT modules
	 * 
	 * @parameter
	 * @required
	 */
	private String[] gwtModules;

	/**
	 * Installed location of gwt version. Specify the gwt.home and gwt.os
	 * properties in your settings.xml, then just use gwt.version in your pom,
	 * and this will fill itself
	 * 
	 * @parameter expression="${gwt.home}/gwt-${gwt.os}-${gwt.version}"
	 */
	private File gwtHome;

	/**
	 * String for os: linux, mac, or windows
	 * 
	 * @parameter expression="${gwt.os}"
	 */
	private String gwtOsName;

	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${basedir}/target/${project.build.finalName}"
	 * @required
	 * @readonly
	 */
	private File artifactOutputDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoHelper.ensureOutputDir(artifactOutputDir);

		if (!gwtHome.isDirectory()) {
			throw new MojoExecutionException(
					"Could not find GWT installed at: "
							+ gwtHome.getAbsolutePath());
		}
		final String command = getGwtClasspath()
				+ " com.google.gwt.dev.GWTCompiler -out "
				+ artifactOutputDir.getAbsolutePath() + ' ';
		final Runtime runtime = Runtime.getRuntime();
		for (String module : gwtModules) {
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
				"java -Xmx512m -classpath ");

		for (Object element : project.getCompileSourceRoots()) {
			final String sourceRoot = (String) element;
			classpath.append(sourceRoot).append(File.pathSeparatorChar);
		}
		for (Object element : project.getResources()) {
			Resource resource = (Resource) element;
			classpath.append(resource.getDirectory()).append(
					File.pathSeparatorChar);
		}
		classpath.append(new File(gwtHome, "gwt-user.jar").getAbsolutePath())
				.append(File.pathSeparatorChar);
		classpath.append(
				new File(gwtHome, "gwt-dev-" + gwtOsName + ".jar")
						.getAbsolutePath()).append(File.pathSeparatorChar);
		for (String element : getRuntimeClasspathElements()) {
			classpath.append(element).append(File.pathSeparatorChar);
		}
		classpath.setLength(classpath.length() - 1);
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
}
