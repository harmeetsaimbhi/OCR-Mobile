package com.example.android.photobyintent;

import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Combination {
	private String FilePathStrings;
	private String FileNameStrings;

	public Combination(String FilePathStrings, String FileNameStrings) {
		this.FilePathStrings = FilePathStrings;
		this.FileNameStrings = FileNameStrings;

	}

	public String getFilePathStrings() {
		return this.FilePathStrings;
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

	public String getFileNameStrings() {
		return this.FileNameStrings;
	}

}
