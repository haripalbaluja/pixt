/**
 *  Copyright (C) 2013  : Arthur Paliden
 *  techsupport@hideinplainsight.ca
 *
 *  This file is part of the HIPS - Hide in Plain Sight  - Steganography application.
 *
 *  HIPS is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  HIPS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HIPS; if not, visit there web site at :
 *  http://www.fsf.org/ 
 *  or write to them at Free Software Foundation, Inc., 
 *  51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  If you feel this program is useful and beneficial then please visit our web site:
 *  
 *  http://www.hideinplainsight.ca
 *  
 *  and make a donation to help support the site and the developers in their ongoing work.
 *
 */
package com.example.root.pixt;

public class StegProfile extends Object
{
	public static final String DEFAULT_SEED = "seed phrase";
	
	public static final int MIN_START_X = 1;
	public static final int MIN_START_Y = 1;
	public static final int MIN_DELTA_X = 3;
	public static final int MIN_DELTA_Y = 3;

	public String seed = DEFAULT_SEED;
	public int iSeed = 0;
	public int initialStripCount = 0;
	
	public int startX = 10;
	public int deltaX = 10;
	public int endX = 10;
	
	public int startY = 10;
	public int deltaY = 10;
	public int endY = 10;
	
	
	public static final int USE_DECIMAL = 1;
	
	public String decimalTable = "0123456789";  // min 3,628,800 ways to arrange this	
	public static final String DECIMAL_FORMAT = "%03d";
	public static final String DECIMAL_END_MARKER = "255";
	
	public static final int USE_HEX = 2;
	
	public String hexTable = "0123456789ABCDEF";
	public static final String HEX_FORMAT = "%02x";
	public static final String HEX_END_MARKER = "FF";
		
	public int insertionMode = USE_DECIMAL;

	public StegProfile()
	{
		iSeed = mkSeed( seed );
	}
	
	public void setSeed( int seed )
	{
		iSeed = seed;
	}
	public String getSeed() {
		return seed;
	}

	public void setSeed( String seed )
	{
		this.seed = seed;
		iSeed = mkSeed( seed );
	}
	public int getISeed()
	{
		return( iSeed );
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	public int getEndY(){
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public String getDecimalTable() {
		return decimalTable;
	}

	public void setTranslationTable( String  str )
	{
		if( this.insertionMode == USE_DECIMAL )
			setDecimalTable( str );
		else
			setHexTable( str );
	}
	
	public String getTranslationTable()
	{
		if( this.insertionMode == USE_DECIMAL )
			return( getDecimalTable() );
		else
			return( getHexTable() );
	}
	
	public void setDecimalTable(String decimalTable) {
		this.decimalTable = decimalTable;
	}

	public String getHexTable() {
		return hexTable;
	}

	public void setHexTable(String hexTable) {
		this.hexTable = hexTable;
	}

	public int getInsertionMode() {
		return insertionMode;
	}

	public void setInsertionMode(int endodingFormat) {
		this.insertionMode = endodingFormat;
	}

	public static int mkSeed( String seedPhrase )
	{
		int seed = 10;
		String str = seedPhrase.trim();
		for( int i = 0; i < str.length(); i++ )
		{
			seed += (int)str.charAt(i);
		}
		return( seed );
	}
	public int getMaxColourValue()
	{
		int mcv = 0;
		
		if( this.insertionMode == USE_DECIMAL )
			mcv = 255 - ((getDecimalTable().length() / 3) + 2);
		else
			mcv = 255 - ((getHexTable().length() / 2) + 2);
		
		return( mcv );
	}
	
	public int getMinColourValue()
	{
		int mcv = 0;
		
		if( this.insertionMode == USE_DECIMAL )
			mcv = ((getDecimalTable().length() / 3) + 2);
		else
			mcv = ((getHexTable().length() / 2) + 2);
		
		return( mcv );
	}

	public int getInitialStripCount() {
		return initialStripCount;
	}

	public void setInitialStripCount(int initialStripCount) {
		this.initialStripCount = initialStripCount;
	}

}