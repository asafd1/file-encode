package asaf.david.filenc;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Files;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import asaf.david.filenc.FileZip.ACTION;

/**
 * Unit test for simple App.
 */
public class FileZipTest extends TestCase
{
	private static String TEST_PASSWORD = "pass";
	private static String RESOURCES_DIR = "src\\test\\resources";
	private static File FILE1 = new File(RESOURCES_DIR, "a.txt");
	private static File FILE2 = new File(RESOURCES_DIR, "\\f1\\b.txt");
	private static File FILE3 = new File(RESOURCES_DIR, "\\f2\\a.txt");
	private static String CONTENT1 = "content1";
	private static String CONTENT2 = "content2";
	private static String CONTENT3 = "content3";

	public FileZipTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(FileZipTest.class);
	}

	public void testEncodeSingleFile() throws Exception
	{
		FileZip fz = prepare();
		fz.act(FILE1, ACTION.ENCRYPT);
		assertTrue("coulnd't find resulting zip file", new File(fz.getZipFileName(FILE1)).exists());
	}

	public void testEncodeFolder() throws Exception
	{
		FileZip fz = prepare();
		fz.act(new File(RESOURCES_DIR), ACTION.ENCRYPT);
		assertTrue("coulnd't find resulting zip file", new File(fz.getZipFileName(FILE1)).exists());
		assertTrue("coulnd't find resulting zip file", new File(fz.getZipFileName(FILE2)).exists());
		assertTrue("coulnd't find resulting zip file", new File(fz.getZipFileName(FILE3)).exists());
	}

	public void testDecodeSingleFile() throws Exception
	{
		FileZip fz = prepare();
		fz.act(FILE1, ACTION.ENCRYPT);
		assertTrue("coulnd't find resulting zip file", new File(fz.getZipFileName(FILE1)).exists());

		Files.delete(FILE1.toPath());

		fz.act(new File(fz.getZipFileName(FILE1)), ACTION.DECRYPT);
		assertTrue("coulnd't find extracted file", FILE1.exists());

		String content = readFile(FILE1);
		assertTrue("corrupt file after encode/decode", CONTENT1.equals(content));
	}

	public void testDecodeFolder() throws Exception
	{
		FileZip fz = prepare();
		File f = new File(RESOURCES_DIR);
		fz.act(f, ACTION.ENCRYPT);

		Files.delete(FILE1.toPath());
		Files.delete(FILE2.toPath());
		Files.delete(FILE3.toPath());

		fz.act(f, ACTION.DECRYPT);
		assertTrue("coulnd't find extracted file", new File(fz.getZipFileName(FILE1)).exists());
		assertTrue("coulnd't find extracted file", new File(fz.getZipFileName(FILE2)).exists());
		assertTrue("coulnd't find extracted file", new File(fz.getZipFileName(FILE3)).exists());

		String content1 = readFile(FILE1);
		String content2 = readFile(FILE2);
		String content3 = readFile(FILE3);
		assertTrue("corrupt file after encode/decode", CONTENT1.equals(content1));
		assertTrue("corrupt file after encode/decode", CONTENT2.equals(content2));
		assertTrue("corrupt file after encode/decode", CONTENT3.equals(content3));
	}

	private FileZip prepare() throws Exception
	{
		deleteFile(new File(RESOURCES_DIR));
		FILE1.getParentFile().mkdir();
		FILE2.getParentFile().mkdir();
		FILE3.getParentFile().mkdir();

		createFile(FILE1, CONTENT1);
		createFile(FILE2, CONTENT2);
		createFile(FILE3, CONTENT3);

		FileZip fz = new FileZip();
		fz.setTestPass(TEST_PASSWORD);
		return fz;
	}

	private void deleteFile(File file) throws Exception
	{
		if (file.isDirectory())
		{
			String[] files = file.list();
			for (String subFile : files)
			{
				deleteFile(new File(file, subFile));
			}
		}

		file.delete();
	}

	private void createFile(File file, String content) throws Exception
	{
		PrintStream stream = new PrintStream(file);
		stream.append(content);
		stream.flush();
		stream.close();
	}

	private String readFile(File file) throws Exception
	{
		char[] buff = new char[1024];
		FileReader reader = new FileReader(file);
		int n = reader.read(buff);
		reader.close();
		return String.valueOf(buff).substring(0, n);
	}
}
