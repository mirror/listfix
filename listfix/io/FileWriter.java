/*
 * listFix() - Fix Broken Playlists!
 * Copyright (C) 2001-2010 Jeremy Caron
 * 
 * This file is part of listFix().
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, please see http://www.gnu.org/licenses/
 */

package listfix.io;

/*
============================================================================
= Author:   Jeremy Caron
= File:     FileWriter.java
= Purpose:  Provides methods for writing a playlist to a file
=           and writing out the ini files for this program.
============================================================================
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import listfix.controller.GUIDriver;

import listfix.controller.tasks.WriteMediaLibraryIniTask;
import listfix.model.AppOptions;
import listfix.model.PlaylistHistory;
import listfix.util.UnicodeUtils;

public class FileWriter
{
	private final String br = System.getProperty("line.separator");
	private static final String fs = System.getProperty("file.separator");
	private final String homeDir = System.getProperty("user.home");
	private final String dataDir = homeDir + fs + "listFixData" + fs;

	public static String getRelativePath(File file, File relativeTo)
	{
		try
		{
			StringTokenizer fileTizer = new StringTokenizer(file.getAbsolutePath(), fs);
			StringTokenizer relativeToTizer = new StringTokenizer(relativeTo.getAbsolutePath(), fs);
			List<String> fileTokens = new ArrayList<String>();
			List<String> relativeToTokens = new ArrayList<String>();
			while (fileTizer.hasMoreTokens())
			{
				fileTokens.add(fileTizer.nextToken());
			}
			while (relativeToTizer.hasMoreTokens())
			{
				relativeToTokens.add(relativeToTizer.nextToken());
			}

			// throw away last token from each, don't need the file names for path calculation.
			String fileName = "";
			if (file.isFile())
			{
				fileName = fileTokens.remove(fileTokens.size() - 1);
			}

			// relativeTo is the playlist we'll be writing to, we need to remove the last token regardless...
			relativeToTokens.remove(relativeToTokens.size() - 1);

			int maxSize = fileTokens.size() >= relativeToTokens.size() ? relativeToTokens.size() : fileTokens.size();
			boolean tokenMatch = false;
			for (int i = 0; i < maxSize; i++)
			{
				if (GUIDriver.fileSystemIsCaseSensitive ? fileTokens.get(i).equals(relativeToTokens.get(i)) : fileTokens.get(i).equalsIgnoreCase(relativeToTokens.get(i)))
				{
					tokenMatch = true;
					fileTokens.remove(i);
					relativeToTokens.remove(i);
					i--;
					maxSize--;
				}
				else if (tokenMatch == false)
				{
					// files can not be made relative to one another.
					return file.getAbsolutePath();
				}
				else
				{
					break;
				}
			}

			StringBuilder resultBuffer = new StringBuilder();
			for (int i = 0; i < relativeToTokens.size(); i++)
			{
				resultBuffer.append("..").append(fs);
			}

			for (int i = 0; i < fileTokens.size(); i++)
			{
				resultBuffer.append(fileTokens.get(i)).append(fs);
			}

			resultBuffer.append(fileName);

			return resultBuffer.toString();
		}
		catch (Exception e)
		{
			return file.getAbsolutePath();
		}
	}

	public void writeDefaultIniFilesIfNeeded()
	{
		BufferedWriter output;
		FileOutputStream outputStream;

		File testDir = new File(dataDir);
		if (!testDir.exists())
		{
			testDir.mkdir();
		}

		File testFile = new File(dataDir + "dirLists.ini");
		if (!testFile.exists() || (testFile.exists() && testFile.length() == 0))
		{
			try
			{
				StringBuilder buffer = new StringBuilder();
				AppOptions options = new AppOptions();
				outputStream = new FileOutputStream(dataDir + "dirLists.ini");
				Writer osw = new OutputStreamWriter(outputStream, "UTF8");
				output = new BufferedWriter(osw);
				buffer.append("[Media Directories]").append(br);
				buffer.append("[Media Library Directories]").append(br);
				buffer.append("[Media Library Files]").append(br);
				output.write(buffer.toString());
				output.close();
				outputStream.close();
			}
			catch (Exception e)
			{
				// eat the error and continue
				e.printStackTrace();
			}
		}

		OptionsWriter.writeDefaults();

		testFile = new File(dataDir + "history.ini");
		if (!testFile.exists() || (testFile.exists() && testFile.length() == 0))
		{
			try
			{
				outputStream = new FileOutputStream(dataDir + "history.ini");
				Writer osw = new OutputStreamWriter(outputStream, "UTF8");
				output = new BufferedWriter(osw);
				output.write(UnicodeUtils.getBOM("UTF-8") + "[Recent Playlists]" + br);
				output.close();
				outputStream.close();
			}
			catch (Exception e)
			{
				// eat the error and continue
				e.printStackTrace();
			}
		}
	}

	public void writeMruPlaylists(PlaylistHistory history)
	{
		try
		{
			StringBuilder buffer = new StringBuilder();
			buffer.append("[Recent Playlists]").append(br);
			String[] filenames = history.getFilenames();
			for (int i = 0; i < filenames.length; i++)
			{
				buffer.append(filenames[i]).append(br);
			}
			FileOutputStream outputStream = new FileOutputStream(dataDir + "history.ini");
			Writer osw = new OutputStreamWriter(outputStream, "UTF8");
			BufferedWriter output = new BufferedWriter(osw);
			output.write(UnicodeUtils.getBOM("UTF-8") + buffer.toString());
			output.close();
			outputStream.close();
		}
		catch (IOException e)
		{
			// eat the error and continue
			e.printStackTrace();
		}
	}

	public void writeMediaLibrary(String[] mediaDir, String[] mediaLibraryDirList, String[] mediaLibraryFileList)
	{
		try
		{
			WriteMediaLibraryIniTask thisTask = new WriteMediaLibraryIniTask(mediaDir, mediaLibraryDirList, mediaLibraryFileList);
			thisTask.start();
		}
		catch (Exception e)
		{
			// eat the error and continue
			e.printStackTrace();
		}
	}
}
        