package com.ds160607.crazyinput.client;

public class Letter {	
	private String chr = "";	
	private double width = 0;
	
	/**
	 * Create a Letter 
	 * @param chr - a char which represent this Letter
	 */
	public Letter(String chr) {		
		setChar(chr);
	}
	public Letter(String chr, double wd) {		
		setChar(chr);		
		this.width = wd;
	}
	
	/**
	 * Set a char which represent this Letter
	 * @param chr String
	 */
	public void setChar(String chr) {
		this.chr = chr;
		if (this.chr.length()>1) {
			this.chr = this.chr.substring(0,1);
		}
	}
	
	/**
	 * Set a width of this letter
	 * @param w - double
	 */
	public void setWidth(double w) {
		this.width = w;
	}
	/**
	 * Get a char which represent this Letter
	 * @return String
	 */
	public String getChar() {
		return this.chr;
	}

	/**
	 * Get a width of this letter
	 * @return double
	 */
	public double getWidth() {
		return this.width;
	}	
}
