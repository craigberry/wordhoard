package edu.northwestern.at.wordhoard.swing.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import java.rmi.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;
import edu.northwestern.at.wordhoard.swing.annotations.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;

/**	A work part text component.
 */

public class PartTextComponent extends WrappedTextComponent {

	/**	Default line number interval (0 = no line numbers, 1 = all line
	 *	numbers, n > 1 = every n'th line number or stanza numbering).
	 */

	private static int defaultLineNumberInterval = 5;

	/**	Parent window. */

	private static AbstractWorkPanelWindow parentWindow;

	/**	The current work part, or null if none. */

	private WorkPart part;

	/**	The annotation model. */

	private AnnotationModel annotationModel;

	/**	The annotations. */

	private TextAttachment[] annotations;

	/**	Tool tip. */

	private JToolTip toolTip;

	/**	Tool tip font. */

	private Font toolTipFont;

	/**	Font manager. */

	private FontManager fontManager = new FontManager();

	/**	Creates a new empty work part text component.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	public PartTextComponent (AbstractWorkPanelWindow parentWindow) {
		this.parentWindow = parentWindow;
		annotationModel = parentWindow.getAnnotationModel();
		this.annotationModel = annotationModel;
		setBackground(Color.white);
		setToolTipText("");
		setSelectable(true);

		annotationModel.addListener(
			new AnnotationAdapter() {
				public void markersShownOrHidden (boolean show)
					throws PersistenceException
				{
					showAnnotationMarkers(show);
				}
				public void annotationSet (int index, Attachment annotation) {
					setAnnotation(index, annotation);
				}

				public void reset () {
					resetPart(null);
				}
			}
		);
	}

	/**	Sets the work part.
	 *
	 *	@param	part	The work part.
	 *
	 *	@param	word	The word in the part to scroll to, or
	 *					null if none.
	 */

	public void setPart (WorkPart part, Word word) {
		TextRange range = word == null ? null : word.getLocation();
		if (part.equals(this.part)) {
			if (range != null) scrollTo(range, false);
		} else {
			setNewPart(part, range, true);
		}
	}

	/**	reset
	 *
	 *	@param	range				The text range to scroll to in base
	 *								coordinates, or null if none.
	 */

	public void resetPart (final TextRange range)
	{
		annotations = null;
		annotationModel.clear();

		clear();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run () {
					try {
						loadPart(part, range, true);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
	}

	/**	Sets a new work part.
	 *
	 *	@param	part				The work part.
	 *
	 *	@param	range				The text range to scroll to in base
	 *								coordinates, or null if none.
	 *
	 *	@param	resetAnnotations	True to reset annotation state.
	 */

	private void setNewPart (final WorkPart part, final TextRange range,
		final boolean resetAnnotations)
	{
		this.part = null;
		if (resetAnnotations) {
			annotations = null;
			annotationModel.clear();
		}
		clear();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run () {
					try {
						loadPart(part, range, resetAnnotations);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
	}

	/**	Loads a new part.
	 *
	 *	@param	part				The work part.
	 *
	 *	@param	range				The text range to scroll to in base
	 *								coordinates, or null if none.
	 *
	 *	@param	resetAnnotations	True to reset annotation state.
	 *
	 *	@throws	Exception	Word longer than right margin.
	 */

	private void loadPart (WorkPart part, TextRange range,
		boolean resetAnnotations)
			throws Exception
	{
		boolean hasStanzaNumbers = part.getHasStanzaNumbers();
		parentWindow.setNumberStanzas(hasStanzaNumbers);
		Text primaryText = part.getPrimaryText().getText();
		boolean hasLineNumbers = primaryText.hasLineNumbers();
		boolean hasMarginalia = primaryText.hasMarginalia();
		int rightMargin;
		if (hasLineNumbers) {
			rightMargin = TextParams.RIGHT_MARGIN_NUMBERS;
		} else if (hasMarginalia) {
			rightMargin = TextParams.RIGHT_MARGIN_MARGINALIA;
		} else {
			rightMargin = TextParams.RIGHT_MARGIN_PLAIN;
		}
		Text[] translations = getTranslations(part);
		Text baseText;
		if (translations == null) {
			baseText = primaryText;
		} else {
			baseText = new TranslatedText(primaryText, translations);
		}
		fontManager.initText(baseText);
		DrawingContext context = new DrawingContext(rightMargin);
		if (hasLineNumbers) {
			FontInfo lineNumberFontInfo =
				fontManager.getWorkFontInfo(TextParams.ROMAN,
					TextParams.LINE_NUMBERS_SIZE);
			int lineNumberInterval = defaultLineNumberInterval;
			if (lineNumberInterval > 1 && hasStanzaNumbers)
				lineNumberInterval = -1;
			context.setLineNumbers(lineNumberInterval,
				lineNumberFontInfo, TextParams.LINE_NUMBERS_RIGHT,
				Color.gray);
		}
		if (hasMarginalia) {
			context.setMarginaliaMargins(
				TextParams.MARGINALIA_LEFT,
				TextParams.MARGINALIA_RIGHT);
		}
		FontInfo markerFontInfo = fontManager.getWorkFontInfo(
			TextParams.ROMAN, TextParams.NOMINAL_FONT_SIZE);
		context.setMarkerInformation(TextParams.MARKER_OFFSET,
			"\u2022", markerFontInfo, Color.blue);
		if (annotationModel.markersShown()) {
			annotations = getAnnotations(part);
			annotationModel.setAnnotations(annotations);
			TextLocation[] annotationStartLocations =
				getAnnotationStartLocations();
			setText(baseText, context, annotationStartLocations);
		} else {
			setText(baseText, context);
		}
		this.part = part;
		if (range != null) scrollTo(range, true);
		if (resetAnnotations) annotationModel.setIndex(0);
	}

	/**	Gets the annotation start locations.
	 *
	 *	@return		Array of annotation start locations.
	 */

	private TextLocation[] getAnnotationStartLocations () {
		int numAnnotations = annotations.length;
		TextLocation[] annotationStartLocations =
			new TextLocation[numAnnotations];
		for (int i = 0; i < numAnnotations; i++) {
			TextAttachment annotation = annotations[i];
			annotationStartLocations[i] =
				annotation.getTarget().getStart();
		}
		return annotationStartLocations;
	}

	/**	Gets the annotations for a part.
	 *
	 *	@param	part		The work part.
	 *
	 *	@return				Array of annotations for the work part, sorted
	 *						into increasing order by starting target location.
	 *
	 *	@throws	PersistenceException
	 */

	private TextAttachment[] getAnnotations (final WorkPart part)
		throws PersistenceException
	{
		PersistenceManager pm = WordHoard.getPm();
		Collection annotationCollection = pm.getAnnotationsForWorkPart(part);
		Collection authoredAnnots = AnnotationUtils.getAnnotations(part.getTag());
		if(authoredAnnots!=null) annotationCollection.addAll(authoredAnnots);
		TextAttachment[] annotations =
			(TextAttachment[])annotationCollection.toArray(
				new TextAttachment[annotationCollection.size()]);

		Arrays.sort(annotations,
			new Comparator() {
				public int compare (Object o1, Object o2) {
					TextAttachment annotation1 = (TextAttachment)o1;
					TextAttachment annotation2 = (TextAttachment)o2;
					TextRange target1 = annotation1.getTarget();
					TextRange target2 = annotation2.getTarget();
					TextLocation start1 = target1.getStart();
					TextLocation start2 = target2.getStart();
					int k = start1.compareTo(start2);
					if (k != 0) return k;
					Long id1 = annotation1.getId();
					Long id2 = annotation2.getId();
					return id1.compareTo(id2);
				}
			}
		);

		return annotations;
	}

	/**	Gets the translations for a part.
	 *
	 *	@param	part		The work part.
	 *
	 *	@return				Array of text objects for selected translations,
	 *						or null if none.
	 */

	private Text[] getTranslations (WorkPart part) {
		Map translations = part.getTranslations();
		if (translations == null) return null;
		int numTranslations = translations.size();
		if (numTranslations == 0) return null;
		java.util.List translationNames =
			TranslationsDialog.getSelectedTranslations(
				part.getWork().getCorpus());
		if (translationNames == null || translationNames.size() == 0)
			return null;
		ArrayList resultList = new ArrayList();
		for (Iterator it = translationNames.iterator(); it.hasNext(); ) {
			String translationName = (String)it.next();
			TextWrapper wrapper =
				(TextWrapper)translations.get(translationName);
			if (wrapper != null) resultList.add(wrapper.getText());
		}
		if (resultList.size() == 0) return null;
		return (Text[])resultList.toArray(new Text[resultList.size()]);
	}

	/**	Sets the line number interval.
	 *
	 *	<p>The line number interval controls the display of line numbers.
	 *	0 = no line numbers, 1 = every line numbered, n = every n'th line
	 *	numbered, -1 = stanza numbers.
	 *
	 *	@param	lineNumberInterval		The new line number interval.
	 */

	public void setLineNumberInterval (int lineNumberInterval) {
		defaultLineNumberInterval = lineNumberInterval < 0 ? 5 :
			lineNumberInterval;
		super.setLineNumberInterval(lineNumberInterval);
	}

	/**	Changes the translations.
	 *
	 *	@throws	Exception
	 */

	public void changeTranslations ()
		throws Exception
	{
		TextRange range = getSelection();
		if (range == null || range.isEmpty()) {
			JViewport viewport = getViewport();
			if (viewport == null) {
				range = null;
			} else {
				Rectangle r = viewport.getViewRect();
				if (r.y == 0) {
					range = null;
				} else {
					int y = r.y + r.height/2;
					Point p = new Point(0, y);
					TextLocation loc = getLocationOfPoint(p, true);
					range = new TextRange(loc);
				}
			}
		}
		setNewPart(part, range, false);
	}

	/**	Creates the tool tip component.
	 *
	 *	<p>We override this method to set the appropriate font for the
	 *	character set in the tool tip component.
	 *
	 *	@return		Tool tip component.
	 */

	public JToolTip createToolTip () {
		toolTip = super.createToolTip();
		if (toolTipFont != null) toolTip.setFont(toolTipFont);
		return toolTip;
	}

	/**	Gets the tool tip location.
	 *
	 *	@param	event		Mouse event.
	 *
	 *	@return				Tool tip location.
	 */

	public Point getToolTipLocation (MouseEvent event) {
		Point p = event.getPoint();
		p.y += 10;
		return p;
	}

	/**	Gets the tool tip text.
	 *
	 *	@param	event		Mouse event.
	 *
	 *	@return				Tool tip text, or null if none.
	 */

	public String getToolTipText (MouseEvent event) {
		try {
			if (!event.isControlDown()) return null;
			if (part == null) return null;
			Point p = event.getPoint();
			TextLocation loc = getLocationOfPoint(p, false);
			int lineIndex = loc.getIndex();
			int charOffsetInLine = loc.getOffset();
			if (lineIndex == -1) return null;
			if (lineIndex == Integer.MAX_VALUE) return null;
			if (charOffsetInLine == -1) return null;
			if (charOffsetInLine == Integer.MAX_VALUE) return null;
			Word word = WordHoard.getPm().findWord(part, lineIndex,
				charOffsetInLine, charOffsetInLine);
			if (word == null) return null;
			Spelling spelling = word.getBriefDescription();
			String str = spelling.getString();
			if (str.length() > 70) str = str.substring(0,70) + " ...";
			toolTipFont = fontManager.getFont(
				spelling.getCharset(), 11);
			if (toolTip != null) toolTip.setFont(toolTipFont);
			return str;
		} catch (Exception e) {
			Err.err(e);
			return null;
		}
	}

	/**	Shows or hides annotation markers.
	 *
	 *	@param	show		True to show markers, false to hide them.
	 *
	 *	@throws	PersistenceException
	 */

	private void showAnnotationMarkers (boolean show)
		throws PersistenceException
	{
		if (part == null) return;
		if (show) {
			if (annotations == null) {
				annotations = getAnnotations(part);
				annotationModel.setAnnotations(annotations);
			}
			TextLocation[] annotationStartLocations =
				getAnnotationStartLocations();
			setMarkers(annotationStartLocations);
		} else {
			setMarkers(null);
		}
	}

	/**	Sets the current annotation.
	 *
	 *	@param	index		Index.
	 *
	 *	@param	annotation	Annotation.
	 */

	private void setAnnotation (int index, Attachment annotation) {
		if (annotation != null) {
			TextAttachment textAnnotation = (TextAttachment)annotation;
			TextRange target = textAnnotation.getTarget();
			scrollTo(target, false);
			int targetFirstLineIndex = target.getStart().getIndex();
			int j = index-1;
			while (j >= 0) {
				target = annotations[j].getTarget();
				if (target.getStart().getIndex() != targetFirstLineIndex) break;
				j--;
			}
			j++;
			int k = index+1;
			while (k < annotations.length) {
				target = annotations[k].getTarget();
				if (target.getStart().getIndex() != targetFirstLineIndex) break;
				k++;
			}
			annotationModel.setExtraMessage(
				(index-j+1) + " of " + (k-j) + " in line");
		} else {
			annotationModel.setExtraMessage(" ");
		}
	}

	/**	Handles a marker click.
	 *
	 *	@param	range	Text range of line containing marker clicked,
	 *					in base coordinates.
	 *
	 *	@throws	PersistenceException
	 */

	public void handleMarkerClick (TextRange range)
		throws PersistenceException
	{
		if (annotations == null) return;
		for (int i = 0; i < annotations.length; i++) {
			TextAttachment annotation = annotations[i];
			TextRange target = annotation.getTarget();
			TextLocation targetStart = target.getStart();
			if (range.contains(targetStart)) {
				annotationModel.showPanel(true);
				annotationModel.setIndex(i);
				break;
			}
		}
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

