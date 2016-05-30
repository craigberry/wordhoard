package edu.northwestern.at.utils.swing.plaf.smoothwindows;

/*	Please see the license information at the end of this file. */

import com.sun.java.swing.plaf.windows.*;
import javax.swing.*;

import edu.northwestern.at. utils.*;

/**
 * Smooth Windows Look And Feel.
 *
 * <p>
 * An enhanced version of the standard Windows Look And Feel by
 * Sun Microsystems. This version uses the capabilities of the Java 2D API
 * to add anti-aliased text to the controls.
 * </p>
 *
 * <p>
 * Based upon the "smoothmetal" look and feel by Marcel Offermans which
 * extends the Metal look and feel instead of the Windows look and feel.
 * </p>
 */

public class SmoothWindowsLookAndFeel extends WindowsLookAndFeel
{
	public static final String smoothPackage =
		ClassUtils.packageName( SmoothWindowsLookAndFeel.class.getName() ) + ".";

	protected void initComponentDefaults( UIDefaults uidefaults )
	{
		super.initComponentDefaults( uidefaults );
	}

	protected void initClassDefaults( UIDefaults uidefaults )
	{
		super.initClassDefaults( uidefaults );

		// Create map of all the classes we provide and install them.

		Object classMap[] =
		{
			"ButtonUI", smoothPackage + "SmoothButtonUI",
			"CheckBoxUI", smoothPackage + "SmoothCheckBoxUI",
			"CheckBoxMenuItemUI", smoothPackage + "SmoothCheckBoxMenuItemUI",
			"ComboBoxUI", smoothPackage + "SmoothComboBoxUI",
			"DesktopIconUI", smoothPackage + "SmoothDesktopIconUI",
			"EditorPaneUI", smoothPackage + "SmoothEditorPaneUI",
			"FileChooserUI", smoothPackage + "SmoothFileChooserUI",
			"InternalFrameUI", smoothPackage + "SmoothInternalFrameUI",
			"LabelUI", smoothPackage + "SmoothLabelUI",
			"MenuUI", smoothPackage + "SmoothMenuUI",
			"MenuBarUI", smoothPackage + "SmoothMenuBarUI",
			"MenuItemUI", smoothPackage + "SmoothMenuItemUI",
			"PasswordFieldUI", smoothPackage + "SmoothPasswordFieldUI",
			"ProgressBarUI", smoothPackage + "SmoothProgressBarUI",
			"RadioButtonUI", smoothPackage + "SmoothRadioButtonUI",
			"RadioButtonMenuItemUI", smoothPackage + "SmoothRadioButtonMenuItemUI",
			"ScrollBarUI", smoothPackage + "SmoothScrollBarUI",
			"ScrollPaneUI", smoothPackage + "SmoothScrollPaneUI",
			"SplitPaneUI", smoothPackage + "SmoothSplitPaneUI",
			"SeparatorUI", smoothPackage + "SmoothSeparatorUI",
			"TabbedPaneUI", smoothPackage + "SmoothTabbedPaneUI",
			"TextAreaUI", smoothPackage + "SmoothTextAreaUI",
			"TextFieldUI", smoothPackage + "SmoothTextFieldUI",
			"TextPaneUI", smoothPackage + "SmoothTextPaneUI",
			"ToggleButtonUI", smoothPackage + "SmoothToggleButtonUI",
			"ToolBarUI", smoothPackage + "SmoothToolBarUI",
			"ToolTipUI", smoothPackage + "SmoothToolTipUI",
			"TreeUI", smoothPackage + "SmoothTreeUI"
		};

		uidefaults.putDefaults( classMap );
	}

	public String getID()
	{
		return "SmoothWindows";
	}

	public String getDescription()
	{
		return "The Smooth Windows Look and Feel";
	}

	public String getName()
	{
		return "SmoothWindows";
	}

	public boolean isNativeLookAndFeel()
	{
		return true;
	}

	public boolean isSupportedLookAndFeel()
	{
		return System.getProperty( "os.name" ).toLowerCase().startsWith( "windows" );
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

