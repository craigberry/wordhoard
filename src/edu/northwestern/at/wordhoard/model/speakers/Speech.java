package edu.northwestern.at.wordhoard.model.speakers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A speech.
 *
 *	<p>Each speech has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.WorkPart
 *		work part} in which the speech occurs.
 *	<li>A set of the
 *		{@link edu.northwestern.at.wordhoard.model.speakers.Speaker
 *		speakers} of the speech.
 *	<li>The common
 *		{@link edu.northwestern.at.wordhoard.model.wrappers.Gender gender}
 *		of all the speakers.
 *		If all speakers in the speech have the same gender,
 *		that is the common gender.  If not all speakers
 *		have the same gender, the common gender is set to
 *		uncertain, mixed, or unknown.
 *	<li>The common
 *		{@link edu.northwestern.at.wordhoard.model.wrappers.Mortality
 *		mortality} of all the speakers.
 *		If all speakers in the speech have the same mortality,
 *		that is the common mortality.  If not all speakers
 *		have the same mortality, the common mortality is set to
 *		unknown or other.
 *	</ul>
 *
 *	@hibernate.class table="speech"
 */

public class Speech implements PersistentObject, SearchDefaults {

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The work part. */

	private WorkPart workPart;

	/**	Set of speakers. */

	private Set speakers = new HashSet();

	/**	Common gender for speakers. */

	private Gender gender = new Gender(Gender.UNCERTAIN_MIXED_OR_UNKNOWN);

	/**	Common mortality for speakers. */

	private Mortality mortality = new Mortality(Mortality.UNKNOWN_OR_OTHER);

	/**	Creates a new speech.
	 */

	public Speech () {
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="assigned"
	 */

	public Long getId () {
		return id;
	}
	
	/**	Sets the unique id.
	 *
	 *	@param	id		The unique id.
	 */
	 
	public void setId (Long id) {
		this.id = id;
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="workPart_index"
	 */

	public WorkPart getWorkPart () {
		return workPart;
	}

	/**	Sets the work part.
	 *
	 *	@param	workPart	The work part.
	 */

	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
	}

	/**	Gets the speakers.
	 *
	 *	@return			The speakers as an unmodifiable set.
	 *
	 *	@hibernate.set name="speakers" table="speech_speakers"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="speech_id"
	 *	@hibernate.collection-many-to-many column="speaker_id"
	 *		class="edu.northwestern.at.wordhoard.model.speakers.Speaker"
	 */

	public Set getSpeakers () {
		return Collections.unmodifiableSet(speakers);
	}

	/**	Adds a speaker.
	 *
	 *	@param	speaker		The new speaker.
	 */

	public void addSpeaker (Speaker speaker) {
		speakers.add(speaker);
		updateCommonGenderAndMortality();
	}

	/**	Gets the gender.
	 *
	 *	@return		The gender.
	 *
	 *	@hibernate.component prefix="gender_"
	 */

	public Gender getGender () {
		return gender;
	}

	/**	Sets the gender.
	 *
	 *	@param	gender	The gender.
	 *
	 *	<p>Needed to make Hibernate happy.</p>
	 */

	public void setGender (Gender gender) {
		this.gender = gender;
	}

	/**	Gets the mortality.
	 *
	 *	@return		The mortality.
	 *
	 *	@hibernate.component prefix="mortality_"
	 */

	public Mortality getMortality () {
		return mortality;
	}

	/**	Sets the mortality.
	 *
	 *	@param	mortality	The mortality.
	 */

	public void setMortality (Mortality mortality) {
		this.mortality = mortality;
	}

	/**	Updates the common gender and mortality values when a speaker is added.
	 *
	 *	<p>
	 *	If all speakers have the gender, the common gender is that gender.
	 *	If the speaker genders are mixed, the common gender is set to
	 *	uncertain, mixed, or unknown.
	 *	</p>
	 *
	 *	<p>
	 *	If all speakers have the mortality, the common gender is that mortality.
	 *	If the speaker mortalities are mixed, the common mortality is set to
	 *	unknown or other.
	 *	</p>
	 *
	 *	<p>
	 *	This method should only be used by the database build programs.
	 *	It should be set protected at the earliest opportunity.
	 *	</p>
	 */

	public void updateCommonGenderAndMortality () {
		if (speakers.size() == 0) {
			setGender(new Gender(Gender.UNCERTAIN_MIXED_OR_UNKNOWN));
			setMortality(new Mortality(Mortality.UNKNOWN_OR_OTHER));
			return;
		}

		Speaker[] speakerArray = (Speaker[])speakers.toArray( new Speaker[]{} );
		Gender commonGender	= speakerArray[0].getGender();
		Mortality commonMortality = speakerArray[0].getMortality();

		for (int i = 1; i < speakerArray.length; i++) {
			if (commonGender != speakerArray[i].getGender() ) {
				commonGender = new Gender(Gender.UNCERTAIN_MIXED_OR_UNKNOWN);
				break;
			}
		}

		for (int i = 1; i < speakerArray.length; i++) {
			if (commonMortality != speakerArray[i].getMortality() ) {
				commonMortality = new Mortality(Mortality.UNKNOWN_OR_OTHER);
				break;
			}
		}

		setGender( commonGender );
		setMortality( commonMortality );
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Speaker.class) || cls.equals(Gender.class) ||
			cls.equals(Mortality.class))
		{
			if (speakers == null || speakers.size() == 0) return null;
			Speaker speaker = (Speaker)speakers.iterator().next();
			return speaker.getSearchDefault(cls);
		} else if (cls.equals(Narrative.class)) {
			return new Narrative(Narrative.SPEECH);
		} else {
			return null;
		}
	}

	/**	Gets grouping objects.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	list		A list. The grouping objects are appended
	 *						to this list.
	 */

	public void getGroupingObjects (Class groupBy, List list) {
		if (groupBy.equals(Speaker.class)) {
			if (speakers != null) {
				for (Iterator it = speakers.iterator(); it.hasNext(); ) {
					Speaker speaker = (Speaker)it.next();
					list.add(speaker);
				}
			}
		} else if (speakers != null) {
			for (Iterator it = speakers.iterator(); it.hasNext(); ) {
				Speaker speaker = (Speaker)it.next();
				speaker.getGroupingObjects(groupBy, list);
			}
		}
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		Long workPartId = workPart == null ? null : workPart.getId();
		exporterImporter.print(workPartId);
		byte genderByte = gender == null ? 0 : gender.getGender();
		exporterImporter.print(genderByte);
		byte mortalityByte = mortality == null ? 0 : mortality.getMortality();
		exporterImporter.print(mortalityByte);
		exporterImporter.println();
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two speeches are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Speech)) return false;
		Speech other = (Speech)obj;
		return id.equals(other.getId());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
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

