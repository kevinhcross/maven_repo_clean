package org.kevinhcross.maven.repo_clean;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This goal is a short term workaround for the maven bug MNG-4142.
 * 
 * It will check all the maven-metadata-local.xml in the supplied path of the
 * local Maven repository and change all localCopy values from true to false.
 * 
 */
@Mojo(name = "clean-metadata", defaultPhase = LifecyclePhase.CLEAN)
public class CleanLocalMetadata extends AbstractMojo {
	private static final String LOCAL_COPY_ELEM_NAME = "localCopy";

	private static final String TARGET_FILE_NAME = "maven-metadata-local.xml";

	@Parameter(property = "clean-metadata.repoBaseDir", defaultValue = "${settings.localRepository}", required = true)
	private File repoBaseDir;

	@Parameter(property = "clean-metadata.relativeRepoPaths", required = true)
	private List<String> relativeRepoPaths;

	private List<File> targetFiles = new ArrayList<File>();

	public void execute() throws MojoExecutionException {
		getLog().info("Local repo base dir: " + repoBaseDir);
		for (String relPath : relativeRepoPaths) {
			File searchBase = new File(repoBaseDir.getAbsoluteFile()
					+ File.separator + relPath);
			getLog().info("Checking files in: " + searchBase);
			if(searchBase.exists() && searchBase.isDirectory()) {
				findTargetFiles(searchBase, TARGET_FILE_NAME);
			}
		}
		for (File targetFile : targetFiles) {
			try {
				fixFile(targetFile);
			} catch (ParserConfigurationException e) {
				throw new MojoExecutionException("Document parsing failure.", e);
			} catch (SAXException e) {
				throw new MojoExecutionException("XML processing error.", e);
			} catch (IOException e) {
				throw new MojoExecutionException("General IO issues.", e);
			} catch (TransformerException e) {
				throw new MojoExecutionException("The document could not be transformed.", e);
			}
		}
	}

	private void fixFile(File targetFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Document doc = parseXml(targetFile);
		alterElement(targetFile, doc);
		writeToFile(targetFile, doc);
	}

	private void alterElement(File targetFile, Document doc) {
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName(LOCAL_COPY_ELEM_NAME);
		for (int i = 0; i < nodeList.getLength(); i++) {
			String localCopyVal = nodeList.item(i).getTextContent();
			if ("true".equalsIgnoreCase(localCopyVal)){
				getLog().info("Changing " + LOCAL_COPY_ELEM_NAME + " in metadata: " + targetFile.getAbsolutePath());
				nodeList.item(i).setTextContent("false");
			}
		}
	}

	private Document parseXml(File targetFile)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		Document doc = docBuilder.parse(targetFile);
		return doc;
	}

	private void writeToFile(File targetFile, Document doc)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(targetFile);
		transformer.transform(source, result);
	}

	private void findTargetFiles(File root, String fileName) {
		for (File f : root.listFiles()) {
			if (f.isDirectory()) {
				findTargetFiles(f, fileName);
			} else {
				if (f.getName().equals(fileName)) {
					targetFiles.add(f);
				}
			}
		}
	}

}
