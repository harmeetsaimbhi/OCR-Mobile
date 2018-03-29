package com.example.android.photobyintent;

import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Combination {
	private String fileName;
	private String filePath;
	private SpannableStringBuilder filterText;


	public Combination(String fileName, String filePath, SpannableStringBuilder filterText) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.filterText = filterText;

	}

	public String getFileName() {
		return this.fileName;
	}

	public SpannableStringBuilder getFilterText() {
		return this.filterText;
	}

	public void setFilterText(SpannableStringBuilder text) {
		this.filterText = text ;
	}

	public String getText(String path) {
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(path)));
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
				br.close();
			} catch (IOException e) {
				throw new Error("Error reading file", e);
			}

		return text.toString();
	}

	public String getFilePath() {
		Log.d("FILEPATH", "The filepath is:" + this.filePath);
		return this.filePath;
	}

	public String getImagePath() {
		Log.d("FILEPATH", "The filepath is:" + this.filePath);
		Log.d("FILEPATH", "The fileName is:" + this.fileName);
		String fileName = this.fileName;
		String imagePath =  (Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES) + "/CameraSample/" + fileName.replace(".txt", ".jpg"));
		Log.d("FILEPATH", "The returned image filepath is:" + imagePath);

		return imagePath;


	}


}
