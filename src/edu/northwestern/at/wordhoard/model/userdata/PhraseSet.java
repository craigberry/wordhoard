package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	A phrase set.
 *
 *  <p>
 *	A phrase set contains a list of phrases.  Typically these
 *	come from various works and do not necessarily appear sequentially.
 *	</p>
 *
 *	<p>
 *	Each phrase set has the following attributes in addition to those defined
 *	for {@link edu.northwestern.at.wordhoard.model.userdata.WordSet word sets}.
 *	</p>
 *
 *	<ul>
 *	<li>A collection of {@link edu.northwestern.at.wordhoard.model.userdata.Phrase
 *		phrases}.
 *		</li>
 *	<li>The sum of the phrase lengths.  Used to compute average phrase length.
 *		</li>
 *	</ul>
 *
 *	@hibernate.subclass table="wordset" discriminator-value="0"
 */

public class PhraseSet
	extends WordSet
	implements
		java.io.Serializable,
		CanCountWords,
		CanCountPhrases,
		PersistentObject,
		SearchCriterion,
		UserDataObject
{
	/**	Collection of phrases belonging to this phrase set.
	 *
	 *	<p>
	 *	Element type is Phrase.
	 *	</p>
	 */

	protected Collection phrases		= new HashSet();

	/**	Sum of the phrase lengths.  Used to compute average phrase length.
	 */

	protected double sumPhraseLengths	= 0.0D;

	/**	Create an empty phrase set.
	 */

	public PhraseSet()
	{
		this.sumPhraseLengths	= 0.0D;
	}

	/**	Create an empty phrase set with a specified name.
	 *
	 *	@param	title		The phrase set's title.
	 *	@param	description	The phrase set's description.
	 *	@param	webPageURL	The phrase set's web page URL.
	 *	@param	owner		The phrase set's owner.
	 *	@param	isPublic	True if the phrase set is public.
	 */

	public PhraseSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic
	)
	{
		this.title					= title;
		this.description			= description;
		this.webPageURL				= webPageURL;
		this.owner					= owner;
		this.isPublic				= isPublic;
		this.isActive				= false;
		this.query      			= "";
		this.sumPhraseLengths		= 0.0D;
		this.creationTime			= new Date();
		this.modificationTime		= this.creationTime;
	}

	/**	Create an empty phrase set with a specified name and query string.
	 *
	 *	@param	title		The phrase set's title.
	 *	@param	description	The phrase set's description.
	 *	@param	webPageURL	The phrase set's web page URL.
	 *	@param	owner		The phrase set's owner.
	 *	@param	isPublic	True if the phrase set is public.
	 *	@param	query		Query properties for generating this phrase set.
	 */

	public PhraseSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query
	)
	{
		this.title					= title;
		this.description			= description;
		this.webPageURL				= webPageURL;
		this.owner					= owner;
		this.isPublic				= isPublic;
		this.isActive				= false;
		this.query					= query;
		this.sumPhraseLengths		= 0.0D;
		this.creationTime			= new Date();
		this.modificationTime		= this.creationTime;
	}

	/**	Create a phrase set from a DOM document node.
	 *
	 *	@param	phraseSetNode	The root node for the phrase set.
	 *	@param	owner			The phrase set's owner.
	 */

	public PhraseSet( org.w3c.dom.Node phraseSetNode , String owner )
	{
		setFromDOMDocumentNode( phraseSetNode );

		this.owner				= owner;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();
		this.isActive			= false;
	}

	/**	Get the phrases.
	 *
	 *	@return			The phrases as an unmodifiable collection.
	 *
	 *	@hibernate.set name="phrases" table="phraseset_phrases"
	 *		inverse="false" access="field" lazy="true"
	 *	@hibernate.collection-key column="phraseSetId"
	 *		foreign-key="phrasesetid_index"
	 *	@hibernate.collection-many-to-many
	 *		class="edu.northwestern.at.wordhoard.model.userdata.Phrase"
	 *		column="phraseId" not-null="true" foreign-key="phraseid_index"
	 */

	public Collection getPhrases()
	{
		return Collections.unmodifiableCollection( phrases );
	}

	/**	Get sum of the phrase lengths.
	 *
	 *	@return		The mean phrase length.
	 *
	 *	@hibernate.property access="field"
	 */

	public double getSumPhraseLengths()
	{
		return sumPhraseLengths;
	}

	/**	Get mean phrase length.
	 *
	 *	@return		The mean phrase length.
	 *
	 *	<p>
	 *	The average phrase length is the sum of the phrase lengths
	 *	divided by the number of phrases.
	 *	</p>
	 */

	public double getMeanPhraseLength()
	{
		double	result	= 0.0D;

		int nPhrases	= phrases.size();

		if ( nPhrases > 0 ) result	= sumPhraseLengths / nPhrases;

		return result;
	}

	/**	Get the number of phrases.
	 *
	 *	@return		The number of phrases.
	 */

	public int getPhraseCount()
	{
		return phrases.size();
	}

	/**	Adds a phrase.
	 *
	 *	@param	phrase	The phrase to add.
	 *
	 *	<p>
	 *	The words and works are updated as needed.
	 *	</p>
	 */

	public void addPhrase( Phrase phrase )
	{
		if ( phrase != null )
		{
			phrases.add( phrase );

			sumPhraseLengths	+= phrase.getWordTags().size();

			addWordTags( phrase.getWordTags() );
		}
	}

	/**	Adds phrases from an array of phrases.
	 *
	 *	@param	phraseArray		The phrases as an array.
	 *
	 *	<p>
	 *	The word, work, and work part collections are also updated as needed.
	 *	</p>
	 */

	public void addPhrases( Phrase[] phraseArray )
	{
		if ( phraseArray != null )
		{
			for ( int i = 0 ; i < phraseArray.length ; i++ )
			{
				addPhrase( phraseArray[ i ] );
			}
		}
	}

	/**	Adds phrases from a collection of phrases.
	 *
	 *	@param	phraseCollection	The phrases as a collection.
	 *
	 *	<p>
	 *	The work and work part collection are also updated as needed.
	 *	</p>
	 */

	public void addPhrases( Collection phraseCollection )
	{
		if ( phraseCollection != null )
		{
			for	(	Iterator iterator = phraseCollection.iterator() ;
					iterator.hasNext() ; )
			{
				addPhrase( (Phrase)iterator.next() );
			}
		}
	}

	/**	Removes all the phrases.
	 *
	 *	<p>
	 *	All words, work parts, and works are also removed.
	 *	</p>
	 */

	public void removePhrases()
	{
		phrases.clear();
		removeWords();

		sumPhraseLengths	= 0.0D;
	}

	/**	Add phrase set to DOM document.
	 *
	 *	@param	document		DOM document to which to add phrase set.
	 *							Must not be null.  In most cases,
	 *							this document should have a "wordhoard"
	 *							node as the root element.
	 *
	 *	@return					true if DOM addition successful, false otherwise.
	 */

	public boolean addToDOMDocument
	(
		org.w3c.dom.Document document
	)
	{
		boolean result	= false;

								//	Indicate failure if document is null.

		if ( document == null ) return result;

		long startTime		= System.currentTimeMillis();

								//	Get all words and phrases
								//	in the phrase set.

		Phrase[] phrases	= PhraseSetUtils.getPhrases( this );

		try
		{
			org.w3c.dom.Element phraseSetElement	=
				ExportUtils.addUserDataObjectHeaderToDOM( this , document );

								//	Emit phrases.

			for	( int i = 0 ; i < phrases.length ; i++ )
			{
								//	Get next phrase.

				Phrase phrase	= phrases[ i ];

								//	Add phrase element to document.

				org.w3c.dom.Element phraseElement	=
					document.createElement( "phrase" );

				phraseSetElement.appendChild( phraseElement );

								//	Get words in this phrase.

				ArrayList words	= new ArrayList( phrase.getWordTags() );

								//	Emit word tags in this phrase.

				for	(	Iterator iterator = words.iterator() ;
						iterator.hasNext() ; )
				{
					String wordTag	= (String)iterator.next();

					org.w3c.dom.Element wordElement	=
						document.createElement( "word" );

					org.w3c.dom.Attr tagAttribute	=
						document.createAttribute( "tag" );

					tagAttribute.setValue( wordTag );

					wordElement.setAttributeNode( tagAttribute );

					phraseElement.appendChild( wordElement );
				}
			}

			result	= true;
		}
		catch ( Exception e )
		{
		}

		long endTime	= System.currentTimeMillis() - startTime;
/*
		System.out.println(
			"phrase set " + title + " exported to DOM in " +
			endTime + " ms" );
*/
		 								//	Return success indicator.
		return result;
	}

	/**	Set values from DOM document node.
	 *
	 *	@param	phraseSetNode	DOM document node with phrase set settings.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode( org.w3c.dom.Node phraseSetNode )
	{
		return false;
	}

	/**	Gets a string representation of the phrase set.
	 *
	 *	@return		The title.
	 */

	public String toString()
	{
		return title;
	}

	/**	Gets a detailed string representation of the phrase set.
	 *
	 *	@return		The title.
	 */

	public String toStringDetailed()
	{
		return title;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault( Class cls )
	{
		if ( cls.equals( PhraseSet.class ) )
		{
			return this;
		}
		else
		{
			return null;
		}
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass()
	{
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause()
	{
		return "word.tag in (:phraseSetWordTags)";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg
	(
		org.hibernate.Query q ,
		org.hibernate.Session session
	)
	{
		q.setParameterList( "phraseSetWordTags" , getWordTags() );
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription
	(
		TextLine line,
		FontInfo romanFontInfo,
		FontInfo[] fontInfo
	)
	{
/*
		PrintfFormat descriptionFormat	=
			new PrintfFormat
			(
				WordHoardProperties( "phrasesetdescription" , "phrase set = %s" )
			);

		line.appendRun
		(
			descriptionFormat.sprintf( title ) ,
			romanFontInfo
		);
*/
		line.appendRun( "phrase set = " + title , romanFontInfo );
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase()
	{
//		return WordHoardProperties.getString( "in" , "in" );
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling( int numHits )
	{
		return new Spelling( getTitle() , TextParams.ROMAN );
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>
	 *	The two objects are equal if their ids are equal.
	 *  </p>
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals( Object obj )
	{
		if ( ( obj == null ) || !( obj instanceof PhraseSet ) ) return false;

		PhraseSet other = (PhraseSet)obj;

		return id.equals( other.getId() );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return id.hashCode();
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

