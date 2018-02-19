package com.example.android.photobyintent;

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

	public String getFileNameStrings() {
		return this.FileNameStrings;
	}

}
