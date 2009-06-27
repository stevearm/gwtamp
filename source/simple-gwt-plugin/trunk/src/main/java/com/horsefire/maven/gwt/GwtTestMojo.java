package com.horsefire.maven.gwt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * @goal test
 * @requiresDependencyResolution runtime
 */
public class GwtTestMojo extends AbstractMojo {

	/**
	 * @parameter
	 * @required
	 */
	private String[] gwtTestSuites;

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
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private DefaultArtifactRepository localRepository;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!gwtHome.isDirectory()) {
			throw new MojoExecutionException(
					"Could not find GWT installed at: "
							+ gwtHome.getAbsolutePath());
		}
		final String command = getGwtClasspath() + " junit.textui.TestRunner ";

		final Runtime runtime = Runtime.getRuntime();
		for (String module : gwtTestSuites) {
			try {
				final Process exec = runtime.exec(command + module);
				final BufferedReader input = new BufferedReader(
						new InputStreamReader(exec.getInputStream()));
				String line = null;
				while ((line = input.readLine()) != null) {
					getLog().info("GWT-Test> " + line);
				}
				final int exitCode = exec.waitFor();
				if (exitCode != 0) {
					throw new MojoExecutionException(
							"GWT Test failed with exit code " + exitCode);
				}
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Exception running GWT test command: '" + command
								+ module + "'", e);
			} catch (InterruptedException e) {
				throw new MojoExecutionException(
						"Exception running GWT test command: '" + command
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
		for (Object element : project.getTestCompileSourceRoots()) {
			final String sourceRoot = (String) element;
			classpath.append(sourceRoot).append(File.pathSeparatorChar);
		}
		for (Object element : project.getResources()) {
			final Resource resource = (Resource) element;
			classpath.append(resource.getDirectory()).append(
					File.pathSeparatorChar);
		}
		classpath.append(new File(gwtHome, "gwt-user.jar").getAbsolutePath())
				.append(File.pathSeparatorChar);
		classpath.append(
				new File(gwtHome, "gwt-dev-" + gwtOsName + ".jar")
						.getAbsolutePath()).append(File.pathSeparatorChar);
		try {
			for (Object element : project.getTestClasspathElements()) {
				final String classpathElement = (String) element;
				classpath.append(classpathElement).append(
						File.pathSeparatorChar);
			}
			for (Object element : project.getDependencyArtifacts()) {
				final DefaultArtifact dependency = (DefaultArtifact) element;
				classpath.append(getFullPathOfDependency(dependency)).append(
						File.pathSeparatorChar);
			}
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException(
					"Exception querying for runtime classpath elements: " + e);
		}
		classpath.setLength(classpath.length() - 1);
		return classpath.toString();
	}

	private String getFullPathOfDependency(DefaultArtifact dep)
			throws MojoExecutionException {
		final StringBuilder pathFromRepo = new StringBuilder();
		pathFromRepo.append(dep.getGroupId().replace('.', File.separatorChar))
				.append(File.separatorChar);
		pathFromRepo.append(dep.getArtifactId()).append(File.separatorChar);
		pathFromRepo.append(dep.getVersion()).append(File.separatorChar);
		pathFromRepo.append(dep.getArtifactId()).append('-').append(
				dep.getVersion()).append('.').append(dep.getType());
		final File depFile = new File(localRepository.getBasedir(),
				pathFromRepo.toString());
		if (!depFile.isFile()) {
			throw new MojoExecutionException("Cannot find test dependency: "
					+ depFile);
		}
		return depFile.getAbsolutePath();
	}
}
