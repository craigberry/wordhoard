package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.tconview.*;

/**	Lists persistent WordHoard classes.
 *
 *	<p>
 *	Provides the full list of Java classes which contain
 *	database-resident data.
 *	</p>
 */

public class PersistentClasses
{
	/**	The list of persistent classes. */

	public final static Class[] persistentClasses =
		new Class[]
		{
			Annotation.class ,
			AnnotationCategory.class ,
			AuthoredTextAnnotation.class ,
			Author.class ,
			BensonLemma.class ,
			BensonLemPos.class ,
			BensonPos.class ,
			Corpus.class ,
			Lemma.class ,
			LemmaCorpusCounts.class ,
			LemmaPosSpellingCounts.class ,
			LemmaWorkCounts.class ,
			LemPos.class ,
			Line.class ,
			MetricalShape.class ,
			Pos.class ,
			Phrase.class ,
			PhraseSetPhraseCount.class ,
			PhraseSetTotalWordFormPhraseCount.class ,
			Speaker.class ,
			Speech.class ,
			TconView.class ,
			TconCategory.class ,
			TextWrapper.class ,
			TotalWordFormCount.class ,
			UserGroup.class,
			UserGroupPermission.class,
			WHQuery.class ,
			Word.class ,
			WordClass.class ,
			WordCount.class ,
			WordPart.class ,
//			WordSearchHelper.class ,
			WordSet.class ,
			WordSetTotalWordFormCount.class ,
			WordSetWordCount.class ,
			WorkPart.class ,
			WorkSet.class
		};
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

