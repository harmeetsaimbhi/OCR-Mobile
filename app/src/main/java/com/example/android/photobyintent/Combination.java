package com.example.android.photobyintent;

import android.text.SpannableStringBuilder;

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
		return this.filePath;
	}

}
