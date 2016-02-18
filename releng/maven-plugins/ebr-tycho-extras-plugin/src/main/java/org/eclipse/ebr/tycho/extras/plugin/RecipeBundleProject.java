/**
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package org.eclipse.ebr.tycho.extras.plugin;

import java.io.File;

import org.eclipse.tycho.ArtifactKey;
import org.eclipse.tycho.ReactorProject;
import org.eclipse.tycho.core.ArtifactDependencyWalker;
import org.eclipse.tycho.core.TychoProject;
import org.eclipse.tycho.core.osgitools.AbstractTychoProject;
import org.eclipse.tycho.core.osgitools.BundleReader;
import org.eclipse.tycho.core.osgitools.OsgiManifest;
import org.eclipse.tycho.core.shared.TargetEnvironment;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 *
 */
@Component(role = TychoProject.class, hint = "eclipse-bundle-recipe")
public class RecipeBundleProject extends AbstractTychoProject {

	private static final String CTX_ARTIFACT_KEY = RecipeBundleProject.class.getName() + "/bundleRecipe/artifactKey";

	private static String sn(final String str) {
		if ((str != null) && !"".equals(str.trim()))
			return str;
		return null;
	}

	@Requirement
	private BundleReader bundleReader;

	@Override
	public ArtifactKey getArtifactKey(final ReactorProject project) {
		final ArtifactKey key = (ArtifactKey) project.getContextValue(CTX_ARTIFACT_KEY);
		if (key == null)
			throw new IllegalStateException("Project has not been setup yet " + project.toString());

		return key;
	}

	private File getBuildDirectory(final MavenProject project) {
		return new File(project.getBuild().getDirectory());
	}

	@Override
	public ArtifactDependencyWalker getDependencyWalker(final MavenProject project) {
		throw new IllegalStateException("not implemented");
	}

	@Override
	public ArtifactDependencyWalker getDependencyWalker(final MavenProject project, final TargetEnvironment environment) {
		throw new IllegalStateException("not implemented");
	}

	private OsgiManifest getManifest(final MavenProject project) {
		return bundleReader.loadManifest(new File(getBuildDirectory(project), "MANIFEST.MF"));
	}

	public String getManifestValue(final String key, final MavenProject project) {
		return getManifest(project).getValue(key);
	}

	public ArtifactKey readArtifactKey(final File location) {
		final OsgiManifest mf = bundleReader.loadManifest(location);
		return mf.toArtifactKey();
	}

	@Override
	public void setupProject(final MavenSession session, final MavenProject project) {
		final ArtifactKey key = getManifest(project).toArtifactKey();
		project.setContextValue(CTX_ARTIFACT_KEY, key);
	}
}
