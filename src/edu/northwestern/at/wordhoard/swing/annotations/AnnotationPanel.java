package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	An annotation panel
 */

class AnnotationPanel extends JPanel {

	/**	Parent window. */

	private AbstractWindow parentWindow;

	/**	Annotation model. */

	private AnnotationModel model;

	/**	The left button control. */

	private JButton leftButton;

	/**	The right button control. */

	private JButton rightButton;

	/** The currently presented annotation. */

	private Attachment currentAnnotation = null;

	/** The currently presented annotation index. */

	private int currentIndex;

	/** The edit button. */

	private JButton edit;

	/** The delete button. */

	private JButton delete;

	/** The author control panel. */

	private JPanel authorControls;

	/**	The category label. */

	private JLabel categoryLabel = new JLabel(" ");

	/**	The annotation number label. */

	private JLabel numberLabel = new JLabel(" ");

	/**	The extra message label. */

	private JLabel extraMessageLabel = new JLabel(" ");

	/**	The wrapped text component. */

	private WrappedTextComponent textComponent;

	/**	The scroll pane. */

	private JScrollPane scrollPane;

	/**	Font manager. */

	private FontManager fontManager;

	/**	Drawing context. */

	private DrawingContext context;

	/**	Creates a new annotation panel.
	 *
	 *	@param	model			Annotation model.
	 *
	 *	@param	parentWindow	The parent window.
	 *
	 *	@param	fontManager		FontManager.
	 */

	AnnotationPanel (final AnnotationModel model, AbstractWindow parentWindow, FontManager fontManager) {

		this.model = model;
		this.fontManager = fontManager;
		this.parentWindow = parentWindow;

		model.addListener(
			new AnnotationAdapter() {
				public void cleared () {
					clearAnnotations();
				}
				public void annotationSet (int index, Attachment annotation) {
					setAnnotation(index, annotation);
				}
				public void extraMessageSet (String message) {
					setExtraMessage(message);
				}

			}
		);

		context = new DrawingContext(
			TextParams.RIGHT_MARGIN_MARGINALIA);
		context.setMarginaliaMargins(
			TextParams.MARGINALIA_LEFT, TextParams.MARGINALIA_RIGHT);

		JLabel titleLabel = new JLabel("Annotations");
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		ImageIcon leftButtonIcon = Images.get("left.gif");
		leftButton = new JButton(leftButtonIcon);
		leftButton.setEnabled(false);
		leftButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						model.prev();
						textComponent.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		leftButton.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

		ImageIcon rightButtonIcon = Images.get("right.gif");
		rightButton = new JButton(rightButtonIcon);
		rightButton.setEnabled(false);
		rightButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						model.next();
						textComponent.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		rightButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

		categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		Font font = numberLabel.getFont();
		font = new Font(font.getName(), font.getStyle(), font.getSize()-2);
		numberLabel.setFont(font);

		extraMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		extraMessageLabel.setFont(font);

		JPanel labels = new JPanel();
		labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
		labels.add(categoryLabel);
		labels.add(Box.createVerticalStrut(4));
		labels.add(numberLabel);
		labels.add(extraMessageLabel);

		JPanel controlsAndLabels = new JPanel();
		controlsAndLabels.setLayout(
			new BoxLayout(controlsAndLabels, BoxLayout.X_AXIS));
		controlsAndLabels.add(leftButton);
		controlsAndLabels.add(Box.createHorizontalGlue());
		controlsAndLabels.add(labels);
		controlsAndLabels.add(Box.createHorizontalGlue());
		controlsAndLabels.add(rightButton);

		textComponent = new WrappedTextComponent();
		textComponent.setSelectable(true);
		textComponent.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

		textComponent.addKeyListener(
			new KeyAdapter() {
				public void keyPressed (KeyEvent event) {
					try {
						int code = event.getKeyCode();
						if (code == KeyEvent.VK_SPACE) {
							scrollDown();
						}
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		textComponent.addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					textComponent.requestFocus();
				}
			}
		);

		addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					textComponent.requestFocus();
				}
			}
		);

		scrollPane = new JScrollPane(
			textComponent,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().setBackground(Color.white);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(10));
		add(titleLabel);
		add(Box.createVerticalStrut(15));
		add(controlsAndLabels);
		add(Box.createVerticalStrut(10));
		add(scrollPane);
		add(Box.createVerticalStrut(5));
		authorControls = createAuthorControls();
		add(authorControls);
		int growSlop = WordHoardSettings.getGrowSlop();
		setBorder(BorderFactory.createEmptyBorder(0,10,growSlop,0));
		Dimension size = new Dimension(510, 510);
		setPreferredSize(size);
	}

	/** Create author panel
	 */
		private JPanel createAuthorControls() {
			JPanel authorControls = new JPanel();
			authorControls.setBackground(Color.RED);

			authorControls.setLayout(new BoxLayout(authorControls, BoxLayout.X_AXIS));

			edit = new JButton("Edit");
			edit.setEnabled(true);
			edit.addActionListener(new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						try {
							final Thread editThread = new Thread (
								new Runnable() {
									public void run () {
										try {
											editAnnotation();
											if (Thread.interrupted()) return;
										} catch (Exception e) {
											Err.err(e);
										}
									}
								}
							);
							editThread.setPriority(editThread.getPriority()-1);
							editThread.start();
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);

			delete = new JButton("Delete");
			delete.setEnabled(true);
			delete.addActionListener(new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						try {
							final Thread deleteThread = new Thread (
								new Runnable() {
									public void run () {
										try {
											deleteAnnotation();
											if (Thread.interrupted()) return;
										} catch (Exception e) {
											Err.err(e);
										}
									}
								}
							);
							deleteThread.setPriority(deleteThread.getPriority()-1);
							deleteThread.start();
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);

		authorControls.add(edit);
		authorControls.add(delete);
		return authorControls;
	 }

	 private void deleteAnnotation() {
		if(currentAnnotation!=null) {
			AnnotationUtils.deleteAnnotation((AuthoredTextAnnotation)currentAnnotation);
			model.resetAnnotations();
		}
	}
	
	// (Defunct)
/*
	 private void deleteRemoteAnnotation() {
		if(currentAnnotation!=null) {
			try {
				String urlstring = WordHoardSettings.getAnnotationWriteServerURL();
				if(urlstring==null) return;

				ClientHttpRequest cr = new ClientHttpRequest(urlstring);
				cr.setParameter("id", currentAnnotation.getId().toString());
				InputStream in = cr.post();
				InputStreamReader inR = new InputStreamReader( in );
				BufferedReader buf = new BufferedReader( inR );
				String line;
				while ( ( line = buf.readLine() ) != null ) {
					System.out.println( line );
				}
				in.close();
				model.resetAnnotations();
			} catch(Exception ex) {
				System.out.println(getClass().getName() + " Exception during deleteAnnotation:" + ex.getMessage());
			}
		}
	 }
*/

	 private void editAnnotation() {
		if(currentAnnotation!=null && (currentAnnotation instanceof AuthoredTextAnnotation)) {
			try {
				AnnotationEditor ae = new AnnotationEditor((AuthoredTextAnnotation)currentAnnotation);
				if(!ae.isCanceled()) {
					model.setIndex(currentIndex);
				//	setAnnotation(currentIndex, currentAnnotation);
				}
			} catch (PersistenceException e) {
				Err.err(e);
				return;
			}
		}
	 }

	/**	Clears the annotations.
	 */

	private void clearAnnotations () {
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);
		categoryLabel.setText(" ");
		numberLabel.setText(" ");
		extraMessageLabel.setText(" ");
		textComponent.clear();
		currentAnnotation=null;
	}

	/**	Sets the current annotation.
	 *
	 *	@param	index		Index.
	 *
	 *	@param	annotation	Attachment.
	 */

	private void setAnnotation (int index, Attachment annotation) {
		int numAnnotations = model.getNumAnnotations();
		if (annotation == null || numAnnotations == 0) {
			leftButton.setEnabled(false);
			rightButton.setEnabled(false);
			categoryLabel.setText(model.getNoAnnotationsMessage());
			numberLabel.setText(" ");
			extraMessageLabel.setText(" ");
			textComponent.clear();
		} else {
			TextWrapped wrapper = annotation.getText();
			Text text = wrapper.getText();
			fontManager.initText(text);
			textComponent.setText(text, context);
			leftButton.setEnabled(index > 0);
			rightButton.setEnabled(index < numAnnotations-1);
			AnnotationCategory category = annotation.getCategory();
			if(category==null && (annotation instanceof AuthoredTextAnnotation)) {
				categoryLabel.setText("User Contributed: " + ((AuthoredTextAnnotation)annotation).getAuthor());
			} else categoryLabel.setText(category.getName());
			numberLabel.setText((index+1) + " of " + numAnnotations);
			textComponent.requestFocus();
		}

		currentAnnotation=annotation;
		currentIndex=index;
		boolean enableAuthorControls = true;

		if(!(annotation instanceof AuthoredTextAnnotation)) {enableAuthorControls=false;}
		else if(WordHoardSettings.getUserID()==null) {enableAuthorControls=false;}
		else if(WordHoardSettings.getCanManageAccounts()) {enableAuthorControls=true;}
//		else if(!WordHoardSettings.getUserID().equals(((AuthoredTextAnnotation)annotation).getOwner()) && !WordHoardSettings.getCanManageAccounts()) {enableAuthorControls=false;}
		else if(!WordHoardSettings.getUserID().equals(((AuthoredTextAnnotation)annotation).getOwner())) {enableAuthorControls=false;}
//		else if(WordHoardSettings.getAnnotationWriteServerURL()==null) {enableAuthorControls=false;}
//		else if(WordHoardSettings.getAnnotationReadServerURL()==null) {enableAuthorControls=false;}
		authorControls.setVisible(enableAuthorControls);
	}

	/**	Sets the extra message.
	 *
	 *	@param	message		The extra message.
	 */

	private void setExtraMessage (String message) {
		extraMessageLabel.setText(message);
	}

	/**	Scrolls down one block, advancing to the next annotation if at the
	 *	end of the current one.
	 */

	private void scrollDown () {
		JViewport viewport = scrollPane.getViewport();
		Rectangle r = viewport.getViewRect();
		Dimension d = viewport.getViewSize();
		if (r.y >= d.height - r.height) {
			rightButton.doClick();
		} else {
			int y = r.y + textComponent.getScrollableBlockIncrement(r,
				SwingConstants.VERTICAL, 1);
			viewport.setViewPosition(new Point(0, y));
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

