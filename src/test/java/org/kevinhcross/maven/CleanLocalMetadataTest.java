/**
 * 
 */
package org.kevinhcross.maven;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kevinhcross.maven.repo_clean.CleanLocalMetadata;

/**
 * @author kevin
 * 
 */
public class CleanLocalMetadataTest extends AbstractMojoTestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();

	}

	/**
	 * Test method for the clean process when supplied with a List of relative
	 * paths in the repo.
	 * @throws Exception 
	 */
	@Test
	public void testBasicRunWithMapPom() throws Exception {
		File pom = getTestFile("src/test/resources/unit/project-to-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		CleanLocalMetadata cleanLocalMetadata = (CleanLocalMetadata) lookupMojo("clean-metadata", pom);
		assertNotNull(cleanLocalMetadata);
		cleanLocalMetadata.execute();
		fail("Not yet implemented");
	}

}
