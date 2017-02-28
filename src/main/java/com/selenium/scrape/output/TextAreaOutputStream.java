package com.selenium.scrape.output;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

	private JTextArea textControl;

	public TextAreaOutputStream(JTextArea control) {
		// TODO Auto-generated constructor stub
		textControl = control;
	}

	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		textControl.append(String.valueOf((char) b));
	}

}
