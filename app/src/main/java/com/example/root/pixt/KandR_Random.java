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

public class KandR_Random extends Object 
{
   private int seed = 10;
   
   public KandR_Random( int seed )
   {
	   setSeed( seed );
   }
   
   public void setSeed( int seed )
   {
	   this.seed = seed;
   }
   public int rand()
   {
	   int result = 0;
	   
	   seed = seed * 1103515245 +12345;
	   result = (int)Math.abs((seed / 65536) % 32768);
	   
	   return( result );
   }

   public int iRand( int min, int max)
   {
	   int mod = 0;
	   int result = 0;
	   
	   result = rand();
	   mod = max - min;
	   result = (result % mod) + min;
	   
	   return( result );
   }
   
   public void strip( int count )
   {
	   for( int i = 0; i < count ; i++ )
		   rand();
   }
}