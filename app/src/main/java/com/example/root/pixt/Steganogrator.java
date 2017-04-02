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



import java.io.File;

public class Steganogrator
{ 
	private static final boolean USE_PLUS_MINUS = true;
	
	private int SOLID_COLOUR_AVOIDANCE = 1;
	public static final int INSERT = 1;
	public static final int EXTRACT = 2;
	
	public static final int CLOCKWISE = 1;
	public static final int COUNTER_CLOCKWISE = 2;
	
    public int maxColourValue = -1;
    public int minColourValue = -1;
    public KandR_Random generator = null; 
    public int maxTgtColourDelta = 10;
    
    public String translationTable = null;
	private int translationTableLength = 0;
    private int insertionMode = 0;
    private StringBuffer sb;
    
    // Local classes
    
	protected class CharRgb
	{
		int r;
		int g;
		int b;
	}

	// End Local Classes
      
    public String insert( String imageFile, String sourceMessage, StegProfile sProfile, boolean autoResize )
    	throws Exception
    {    	
    	int currX = 0;
    	int currY = 0;    	
    	int deltaX = 0;
    	int deltaY = 0;    	
    	int endX = 0;
    	int endY = 0;    	
    	int messageLength = 0;
    	boolean done = false;
    	int rgb = 0;
    	int pflags = 0;
    	int currRed = 0;
    	int currGreen = 0;
    	int currBlue = 0;

    	int tgtX = 0;
    	int tgtY = 0;
		int tgtRed = 0;
		int tgtGreen = 0;
		int tgtBlue = 0;
		
		int dr = 0;
		int dg = 0;
		int db = 0;	
		int tgtDir = 0;

    	int newRgb = 0;
    	int dx = 0;
    	String message = null;
    	Point p = new Point();
    	CharRgb rgbClass = new CharRgb();
    	
    	generator = new KandR_Random( sProfile.getISeed() );
    	generator.strip( sProfile.getInitialStripCount() );
    	
    	insertionMode = sProfile.getInsertionMode();
    	translationTable = sProfile.getTranslationTable();
    	translationTableLength = translationTable.length();
    	    	
    	Picture picture = new Picture();
    	picture.openForInsert( imageFile, true );
    	
      	//System.out.println( "Steganagrator.insertLoaded<" + imageFile + ">" ); 
    	
    	currX = sProfile.getStartX() + generator.iRand( 0, sProfile.getDeltaX() );
    	currY = sProfile.getStartY() + generator.iRand( 0, sProfile.getDeltaY() );
    	
    	deltaX = sProfile.getDeltaX();
    	deltaY = sProfile.getDeltaY();
    	
    	endX = picture.getWidth() - (sProfile.getEndX() + generator.iRand( 0, sProfile.getDeltaX() ));
    	endY = picture.getHeight() - (sProfile.getEndY()  + generator.iRand( 0, sProfile.getDeltaY() ));
    	   					
		maxColourValue = sProfile.getMaxColourValue();
		minColourValue = sProfile.getMinColourValue();		
		maxTgtColourDelta = maxColorDelta();
		
		//System.out.println( "Steganagrator.insert:SourceMessage <" + sourceMessage + ">" );
		
    	message = translate( sourceMessage );  	  	
    	messageLength = message.length();
    	
     	for( int cpos = 0; cpos < messageLength ; cpos++ )
    	{
			done = false;
			do
			{	
				//rgb = bufferedImage.getRGB( currX, currY );
				rgb = picture.getPixel( currX, currY );
				pflags = (rgb >> 24) & 0xFF;
				currRed = (rgb >> 16) & 0xFF;
				currGreen = (rgb >> 8) & 0xFF;
				currBlue = rgb & 0xFF;
			
				if( currRed < maxColourValue && currGreen < maxColourValue && currBlue < maxColourValue &&
						currRed > minColourValue && currGreen > minColourValue && currBlue > minColourValue	)
				{
					// avoid discreet edges
					boolean foundTarget = false;
					boolean stillLooking = true;
					int startIndex = -1;
					int testIndex = -1;
					do
					{
						if( startIndex == -1 )
						{
							startIndex = getTgtDelta( p, startIndex, 0 );
							if( generator.iRand( 0, 1 ) == 0 )
								tgtDir = CLOCKWISE;
							else
								tgtDir = COUNTER_CLOCKWISE;
						}
						else
						{
							testIndex = getTgtDelta( p, testIndex, tgtDir);
						}
						if( testIndex == startIndex ) // we have gone in complete circle give up looking here
						{
							stillLooking = false;
							//System.out.println(" stop looking" );
						}
						else
						{
							if( testIndex == -1 ) // first time through
								testIndex = startIndex;
							
							tgtX = currX + p.x;
							tgtY = currY + p.y;

							//rgb = bufferedImage.getRGB( tgtX, tgtY );
							rgb = picture.getPixel( tgtX, tgtY );
							
							tgtRed = (rgb >> 16) & 0xFF;
							tgtGreen = (rgb >> 8) & 0xFF;
							tgtBlue = rgb & 0xFF;							
							
							dr = Math.abs( currRed - tgtRed );
							dg = Math.abs( currGreen - tgtGreen );
							db = Math.abs( currBlue - tgtBlue );							
							
							// this edge test may be too simple
							if(  dr < maxTgtColourDelta  && dg < maxTgtColourDelta && db < maxTgtColourDelta )
							{
								if( !(dr == 0 && dg == 0 && db == 0) ) // solid colour ???
									foundTarget = true;
								//else
									//System.out.println(" solid colour" );
							}
							//else
								//System.out.println(" edge colour" );
						}

					}while( !foundTarget && stillLooking );
					
					if( foundTarget )
						done = true;
				}
				
				if( !done )
				{
					currX += generator.iRand( StegProfile.MIN_DELTA_X, deltaX );
					if( currX >= endX )
					{
						currX = sProfile.getStartX();
						currY += generator.iRand( StegProfile.MIN_DELTA_Y, deltaY );
					}
				}			
				if( currY > endY )
				{
					throw new Exception( "No more room in image, encoding failed at character position " + cpos/3 );
				}
			}
			while( !done );
						
			if( tgtX < 0 || tgtX > (endX+1) || tgtY < 0 || tgtY > (endY + 1) )
			{
				throw new Exception( "Unable to locate valid target pixel." );
			}
			
			translateChar( message.charAt( cpos ), rgbClass);
			
			if( rgbClass != null )
			{
				switch( generator.iRand(0,2 ) )
				{
					case 0:
						if( tgtRed > currRed )
							currRed += rgbClass.r;
						else
							currRed -= rgbClass.r;
						
						if( tgtGreen > currGreen )
							currGreen += rgbClass.g;
						else
							currGreen -= rgbClass.g;

						if( tgtBlue > currBlue )
							currBlue += rgbClass.b;
						else
							currBlue -= rgbClass.b;
					break;
					case 1:
						if( tgtRed > currRed )
							currRed += rgbClass.b;
						else
							currRed -= rgbClass.b;
						
						if( tgtGreen > currGreen )
							currGreen += rgbClass.r;
						else
							currGreen -= rgbClass.r;

						if( tgtBlue > currBlue )
							currBlue += rgbClass.g;
						else
							currBlue -= rgbClass.g;
					break;
					case 2:
						if( tgtRed > currRed )
							currRed += rgbClass.g;
						else
							currRed -= rgbClass.g;
						
						if( tgtGreen > currGreen )
							currGreen += rgbClass.b;
						else
							currGreen -= rgbClass.b;

						if( tgtBlue > currBlue )
							currBlue += rgbClass.r;
						else
							currBlue -= rgbClass.r;
					break;
				}
	
				// build new pixel
				
				newRgb = pflags;
				newRgb = (newRgb << 8) + currRed;
				newRgb = (newRgb << 8) + currGreen;
				newRgb = (newRgb << 8) + currBlue;
	
				// write it to target location
				
				//bufferedImage.setRGB( tgtX, tgtY, newRgb);
				picture.setPixel( tgtX, tgtY, newRgb);
				
				// get next pixel
	 
				dx = generator.iRand( StegProfile.MIN_DELTA_X, deltaX ); 			
				if( (currX + dx) >= endX )
				{
					currX = sProfile.getStartX();
					currY += generator.iRand( StegProfile.MIN_DELTA_Y, deltaY );
				}
				else
				{
					currX += dx; 
				}
			}
			else
			{
				cpos = messageLength;
				break;
			}
    	}
    	
    	// save the new image
    	
    	int lastDot = imageFile.lastIndexOf( '.' );
    	imageFile = imageFile.substring( 0, lastDot );

    	File tstFile = null;
		try
		{
			tstFile = new File( imageFile + ".png" );
			if( tstFile.exists() )
			{
				int bndx = imageFile.indexOf( "(" );
				if( bndx > 0 )
					imageFile = imageFile.substring( 0, bndx );
				int i = 0;
				do{
					i++;
					String str = imageFile+ "(" + i + ").png";
					tstFile = new File( str );											
				}while( tstFile.exists() );
			}						
			picture.save( tstFile  );		
		}
		catch( Exception e )
		{
			e.printStackTrace();
			throw e;
		}
		
		//System.out.println( "Save to <" + tstFile + ">" );

		return( tstFile.getAbsolutePath() );
    }
    
    /*
    public String extract( String imageFile, StegProfile sProfile )
    	throws Exception
    {
      	BufferedImage bufferedImage = null;
      	ImageIcon imageIcon = new ImageIcon( imageFile );
		bufferedImage = createBufferedImage( imageIcon.getImage(), imageIcon.getIconWidth(), imageIcon.getIconHeight(), true );
		return( extract( bufferedImage, sProfile ) );
    }
	*/
    
	//public String extract( BufferedImage bufferedImage, StegProfile sProfile )
	public String extract( String imageFile, StegProfile sProfile, boolean htmlOn )
		throws Exception
    {
    	//StringBuffer sb = new StringBuffer();
    	int currX = 0;
    	int currY = 0;    	
    	int deltaX = 0;
    	int deltaY = 0;    	
    	int endX = 0;
    	int endY = 0;    	
    	boolean done = false;
    	int rgb = 0;
    	int currRed = 0;
    	int currGreen = 0;
    	int currBlue = 0;
		int tgtRed = 0;
		int tgtGreen = 0;
		int tgtBlue = 0;
    	int tgtX = 0;
    	int tgtY = 0;
    	int dx = 0;
    	int charIndex = 0;
    	Point p = new Point();
    	char charTest[] = null;
    	char ch = 0;
    	int totalCharCount = 0;
    	int charValue = 0; 
		int dr = 0;
		int dg = 0;
		int db = 0;	
		int tgtDir = 0;

    	sb = new StringBuffer();
    	
    	Picture picture = new Picture();
    	picture.openForExtract( imageFile );
    	
    	generator = new KandR_Random( sProfile.getISeed() );
    	generator.strip( sProfile.getInitialStripCount() );
    	
    	translationTable = sProfile.getTranslationTable();
    	insertionMode = sProfile.getInsertionMode();
    	translationTableLength = translationTable.length();
    	
    	currX = sProfile.getStartX() + generator.iRand( 0, sProfile.getDeltaX() );
    	currY = sProfile.getStartY() + generator.iRand( 0, sProfile.getDeltaY() );
    	
    	deltaX = sProfile.getDeltaX();
    	deltaY = sProfile.getDeltaY();
    	
    	//endX = bufferedImage.getWidth() - (sProfile.getEndX() + generator.iRand( 0, sProfile.getDeltaX() ));
    	//endY = bufferedImage.getHeight() - (sProfile.getEndY()  + generator.iRand( 0, sProfile.getDeltaY() ));
    	endX = picture.getWidth() - (sProfile.getEndX() + generator.iRand( 0, sProfile.getDeltaX() ));
    	endY = picture.getHeight() - (sProfile.getEndY()  + generator.iRand( 0, sProfile.getDeltaY() ));

		maxColourValue = sProfile.getMaxColourValue();
		minColourValue = sProfile.getMinColourValue();		
		maxTgtColourDelta = maxColorDelta();

    	boolean eof = false;
    	totalCharCount = 0;   	
    	if( insertionMode == StegProfile.USE_DECIMAL )
    		 charTest = new char[3];
    	else
    		charTest = new char[2];
    	
    	while( !eof )
    	{
			done = false;
			do
			{
				//rgb = bufferedImage.getRGB( currX, currY );
				rgb = picture.getPixel( currX, currY );
				currRed = (rgb >> 16) & 0xFF;
				currGreen = (rgb >> 8) & 0xFF;
				currBlue = rgb & 0xFF;
				
				if( currRed < maxColourValue && currGreen < maxColourValue && currBlue < maxColourValue &&
						currRed > minColourValue && currGreen > minColourValue && currBlue > minColourValue	)
				{
					// avoid discreet edges

					boolean foundTarget = false;
					boolean stillLooking = true;
					int startIndex = -1;
					int testIndex = -1;
					do
					{
						if( startIndex == -1 )
						{
							startIndex = getTgtDelta( p, startIndex, 0 );
							if( generator.iRand( 0, 1 ) == 0 )
								tgtDir = CLOCKWISE;
							else
								tgtDir = COUNTER_CLOCKWISE;

						}
						else
						{
							testIndex = getTgtDelta( p, testIndex, tgtDir );
						}
						if( testIndex == startIndex ) // we have gone in complete circle give up looking here
						{
							stillLooking = false;
						}
						else
						{
							if( testIndex == -1 ) // first time through
								testIndex = startIndex;
							
							tgtX = currX + p.x;
							tgtY = currY + p.y;

							//rgb = bufferedImage.getRGB( tgtX, tgtY );
							rgb = picture.getPixel( tgtX, tgtY );
							
							tgtRed = (rgb >> 16) & 0xFF;
							tgtGreen = (rgb >> 8) & 0xFF;
							tgtBlue = rgb & 0xFF;
							
							dr = Math.abs( currRed - tgtRed );
							dg = Math.abs( currGreen - tgtGreen );
							db = Math.abs( currBlue - tgtBlue );							
							
							// this edge test may be too simple
							if(  dr < maxTgtColourDelta  && dg < maxTgtColourDelta && db < maxTgtColourDelta )
							{
								if( !(dr == 0 && dg == 0 && db == 0) ) // solid colour ???
									foundTarget = true;
							}
						}

					}while( !foundTarget && stillLooking );
					
					if( foundTarget )
						done = true;
				}
				if( !done )
				{
					currX += generator.iRand( StegProfile.MIN_DELTA_X, deltaX );					
					if( currX >= endX )
					{
						currX = sProfile.getStartX();
						currY += generator.iRand( StegProfile.MIN_DELTA_Y, deltaY );
					}
				}
				
				if( currY > endY )
				{
					exitError( "no end of message");
				}
			}
			while( !done );
			
			charIndex = dr + dg + db - SOLID_COLOUR_AVOIDANCE;  // -1 solid colour avoidance
			generator.strip(1);
							
			ch = valueToChar( charIndex );
			
			if( insertionMode == StegProfile.USE_DECIMAL )
			{
				int intValue = (int)ch - (int)'0';
				switch( totalCharCount % 3 )
				{
					case 0:
						if( ch == '0' || ch == '1' || ch == '2' )
						{
							charTest[0] = ch;
						}
						else
							throw new Exception( "Invalid charTest[0]<" + intValue+ ">" );
					break;
					case 1:
						charTest[1] = ch;
					break;
					case 2:
						charTest[2] = ch;					
						sb.append( charTest );
						if( charTest[0] == '2' && charTest[1] == '5' && charTest[2] == '5' )
							eof = true;
					break;
				}
			}
			else
			{
				switch( totalCharCount % 2 )
				{
					case 0:
						charTest[0] = ch;
					break;
					case 1:
						charTest[1] = ch;
						
						sb.append( charTest );
						if( charTest[0] == 'F' && charTest[1] == 'F' )
							eof = true;
					break;
				}
			}
				
			totalCharCount++;

			if( !eof )
			{
				dx = generator.iRand( StegProfile.MIN_DELTA_X, deltaX );
				if( currX + dx >= endX )
				{					
					currX = sProfile.startX;
					currY += generator.iRand( StegProfile.MIN_DELTA_Y, deltaY );
				}
				else
				{
					currX += dx;
					
				}
			}
    	}
    	
    	return( mkAscii( sb.toString(), htmlOn) );
    }
	
	public String getMessage()
	{
		return( mkAscii( sb.toString(), false ));
	}
	
	public char valueToChar( int charIndex )
		throws Exception
	{
		char ch = '\0';
		
		if( charIndex >= 0 && charIndex < translationTableLength )
		{
			ch = translationTable.charAt( charIndex );
		}
		else
		{
			throw new Exception( "Invalid charIndex<" + charIndex + ">");
		}
		return( ch );
	}
	
	public String mkAscii( String str, boolean htmlOn )
	{
		char ch = '\0';
		String token = null;
		int s = 0;
		
		StringBuffer msg = new StringBuffer();
		
		if( insertionMode == StegProfile.USE_DECIMAL )
		{
			while(  s < str.length() )
			{
				token = str.substring( s,s+3 );
				
				if( !token.equals( StegProfile.DECIMAL_END_MARKER ) )
				{
					ch = (char)Integer.parseInt( token );
					if( ch == '\n' )
					{
						if( htmlOn )
							msg.append( "<br>" );
						else
							msg.append( '\n' );
					}
					else
						msg.append( ch );
					s += 3;
				}
				else
				{
					s = str.length();
				}
			}
		}
		else
		{
			while(  s < str.length() )
			{
				token = str.substring( s,s+2 );
				
				if( !token.equals( StegProfile.HEX_END_MARKER ) )
				{
					ch = (char)Integer.parseInt( token, 16 );
					if( ch == '\n' )
					{
						msg.append( "<br>" );
					}
					else
						msg.append( ch );
					s += 2;
				}
				else
				{
					s = str.length();
				}
			}
		}
		
		return( msg.toString() );

	}
    
    
	public void exitError( String error )
	{
		System.out.println( error );
	}
	
	public void translateChar( char ch, CharRgb rgb )
	{
		int pos = 0;
		
		while( pos < translationTableLength && ch != translationTable.charAt( pos ) )
		{
			pos++;
		}
		
		pos = pos + SOLID_COLOUR_AVOIDANCE;
		
		if( pos < (translationTableLength + SOLID_COLOUR_AVOIDANCE) )
		{
			rgb.r = (int)Math.round( (double)pos/3.0);
			rgb.g = rgb.r;
			rgb.b = pos - (rgb.r+rgb.g);
		}
		else
		{
			System.out.println( "<" + ch + ">" );
			rgb = null;
		}
	}
	
	public String translate( String source )
	{
		return( translate( source, true ) );
	}
	
	public String translate( String source, boolean addTerminator )
	{
		int ch = 0;
		String tmp = null;
		StringBuffer sb = new StringBuffer();
		
		for( int i = 0; i < source.length(); i++ )
		{
			ch = (int)source.charAt(i);
			
			if( ch >= 255 )
			{
				tmp = "&#" + (int)ch + ";" ;				
				tmp = translate( tmp, false );	
				sb.append( tmp );
			}
			else
			{
				if( insertionMode == StegProfile.USE_DECIMAL )
					sb.append( String.format( StegProfile.DECIMAL_FORMAT , ch ) );
				else
					sb.append( String.format( StegProfile.HEX_FORMAT , ch ) );

			}
		}
		
		if( addTerminator )
		{
			if( insertionMode == StegProfile.USE_DECIMAL )
				sb.append( StegProfile.DECIMAL_END_MARKER );
			else
				sb.append( StegProfile.HEX_END_MARKER );
		}
		return( sb.toString().toUpperCase() );
	}
	
	public int getTgtDelta( Point p )
	{
		return( getTgtDelta( p, -1, 0 ) );
	}
	public int getTgtDelta( Point p, int oldIndex, int tgtDir)
	{
		int index = 0;
		
		if( oldIndex == -1 )
			index = generator.iRand( 0, 7);
		else
		{
			if( tgtDir == CLOCKWISE )
			{
				index = oldIndex + 1;
				if( index > 7 )
					index= 0;
			}
			else
			{
				index = oldIndex - 1;
				if( index < 0 )
					index = 7;
			}
		}
		
		switch( index )
		{
			case 0:
				p.x = 1;
				p.y = 0;
			break;
			case 1:
				p.x = 1;
				p.y = 1;
			break;
			case 2:
				p.x = 0;
				p.y = 1;
			break;
			case 3:
				p.x = -1;
				p.y = 1;
			break;
			case 4:
				p.x =-1;
				p.y = 0;
			break;
			case 5:
				p.x = -1;
				p.y = -1;
			break;
			case 6:
				p.x = 0;
				p.y = -1;
			break;
			case 7:
				p.x = 1;
				p.y = -1;
			break;
		}
		return( index );
	}
			
	public int maxColorDelta()
	{
		return(  (translationTableLength / 3) + 2);
	}
	
	public static void main( String[] args )
	{
		String tstStr = "48656C6C6F20576F726C64FF";
		StegProfile sProfile = new StegProfile();
		Steganogrator st = new Steganogrator();
		
		try
		{
			sProfile.setInsertionMode( StegProfile.USE_HEX );
			
			System.out.println( st.translate( "Hello World", true ) );
			System.out.println( st.mkAscii( tstStr, false ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
