package asaf.david.filenc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Hello world!
 *
 */
public class FileZip
{
	enum ACTION
	{
		ENCRYPT, DECRYPT;
	}

	private ACTION m_action;
	private File m_file;
	private String m_password = null;

	public static void main(String[] args)
	{
		FileZip fz = new FileZip();
		fz.doWork(args);

	}

	private void doWork(String[] args)
	{
		readInput(args);

		if (!m_file.exists())
		{
			errorFileNotFound();
		}

		act(m_file, m_action);
	}

	void act(File file, ACTION action)
	{
		if (file.isDirectory())
		{
			String[] files;

			if (action == ACTION.ENCRYPT)
			{
				files = file.list((File dir, String name) -> {
					return (name.indexOf("{z}") == -1);
				});
			}
			else
			{
				files = file.list((File dir, String name) -> {
					return (name.indexOf("{z}") != -1 || new File(dir, name).isDirectory());
				});
			}

			for (String subFile : files)
			{
				act(new File(file, subFile), action);
			}
			return;
		}

		if (action == ACTION.ENCRYPT)
		{
			encryptFile(file);
		}
		else
		{
			decryptFile(file);
		}
	}

	private void decryptFile(File file)
	{
		try
		{
			ZipFile zipFile = new ZipFile(file);
			zipFile.setPassword(getPassword());
			zipFile.extractAll(file.getParent());
		}
		catch (ZipException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void encryptFile(File file)
	{
		ZipOutputStream outputStream = null;
		InputStream inputStream = null;

		try
		{
			// Initiate output stream with the path/file of the zip file
			// Please note that ZipOutputStream will overwrite zip file if it already exists
			outputStream = new ZipOutputStream(new FileOutputStream(new File(getZipFileName(file))));

			ZipParameters parameters = new ZipParameters();

			// Deflate compression or store(no compression) can be set below
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			parameters.setEncryptFiles(true);

			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

			parameters.setPassword(getPassword());

			outputStream.putNextEntry(file, parameters);

			inputStream = new FileInputStream(file);
			byte[] readBuff = new byte[4096];
			int readLen = -1;

			while ((readLen = inputStream.read(readBuff)) != -1)
			{
				outputStream.write(readBuff, 0, readLen);
			}

			// Once the content of the file is copied, this entry to the zip file
			// needs to be closed. ZipOutputStream updates necessary header information
			// for this file in this step
			outputStream.closeEntry();

			inputStream.close();

			// ZipOutputStream now writes zip header information to the zip file
			outputStream.finish();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private String getZipFileName(File file)
	{
		return file.getAbsolutePath() + "{z}.zip";
	}

	private String getPassword()
	{
		if (m_password == null)
		{
			System.out.print("Enter password: ");
			m_password = String.valueOf(System.console().readPassword());
		}

		return m_password;
	}

	private void readInput(String[] args)
	{
		if (args.length < 2)
		{
			errorWrongUsage();
		}

		if (args[0].equals("e"))
		{
			m_action = ACTION.ENCRYPT;
		}
		else if (args[0].equals("d"))
		{
			m_action = ACTION.DECRYPT;
		}
		else
		{
			errorWrongUsage();
		}

		m_file = new File(args[1]);
	}

	private void errorFileNotFound()
	{
		usage();
		System.exit(-2);
	}

	private void errorWrongUsage()
	{
		usage();
		System.exit(-2);
	}

	private void usage()
	{
		System.out.println("Usage: filenc e|d <file>|<dir>");

	}

	void setTestPass(String pass)
	{
		m_password = pass;
	}
}
