package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

/*
 *
 * Copyright 2005 Northwestern University, Inc. All Rights Reserved.
 * 
 * Author: Bill Parod
 *         Information Technology
 *         Northwestern University
 * 	   bill-parod@northwestern.edu
 *
 * Date:   9/8/2005 
 */

public class SearchCriteriaClassMismatchException extends Exception {

   public SearchCriteriaClassMismatchException() {super(); System.out.println("Handling SearchCriteriaClassMismatchException");}

   public SearchCriteriaClassMismatchException(String msg) {
      super(msg);
	   System.out.println("Handling SearchCriteriaClassMismatchException:" + msg);
   }
}

/*
 * <p>
 * Copyright &copy; 2004-2011 Northwestern University.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */

