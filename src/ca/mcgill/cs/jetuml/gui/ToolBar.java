/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.gui;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.IconCreator;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *  A collapsible tool bar than contains various tools and optional
 *  command shortcut buttons. Only one tool can be selected at the time.
 *  The tool bar also controls a pop-up menu with the same tools as 
 *  the tool bar.
 *  
 *  @author Martin P. Robillard
 */

// Do we need a separate button for collapsed and expanded?
// Rename pToolTip to pLabel?
// STYLE CHECKER

@SuppressWarnings("serial")
public class ToolBar extends JFXPanel
{
//	private static final int BUTTON_SIZE = 25;
	private static final int BUTTON_SIZE = 50;
	private static final int H_PADDING = 5;
	private static final int FONT_SIZE = 14;
	private static final String EXPAND = "<<";
	private static final String COLLAPSE = ">>";
	
	private ArrayList<ToggleButton> aButtons = new ArrayList<>();
	private ArrayList<ToggleButton> aButtonsEx = new ArrayList<>();
//	private JPanel aToolPanel = new JPanel(new VerticalLayout());
//	private JPanel aToolPanelEx = new JPanel(new VerticalLayout());
	private JFXPanel aToolPanel = new JFXPanel();
	private JFXPanel aToolPanelEx = new JFXPanel();
	
	// NEEDED HERE??
	private BorderPane aLayout = new BorderPane();
	private BorderPane aLayoutEx = new BorderPane();
	
	private VBox aToolsLayout = new VBox();
	private VBox aToolsLayoutEx = new VBox();
	// Add vBox to border pane
	private ArrayList<GraphElement> aTools = new ArrayList<>();
	private JPopupMenu aPopupMenu = new JPopupMenu();
	
	private Scene aScene;

	/**
     * Constructs the tool bar.
     * @param pGraph The graph associated with this tool bar.
	 */
	public ToolBar(Graph pGraph)
	{
		ToggleGroup group = new ToggleGroup();
		ToggleGroup groupEx = new ToggleGroup();
		
		// Adjust the layout (NEED TO COMPLETE)
		aToolsLayout.setPadding(new Insets(10,10,10,10));
		aLayout.setCenter(aToolsLayout);
		
		aToolsLayoutEx.setPadding(new Insets(10,10,10,10));
		aLayoutEx.setCenter(aToolsLayoutEx);
		
		
		createSelectionTool(group, groupEx);
		createNodesAndEdgesTools(pGraph, group, groupEx);
		
		//ADJUST AFTER MAIN IS DONE
		addCopyToClipboard();
		createExpandButton();
//		freeCtrlTab();

		add(aToolPanel, BorderLayout.CENTER);
		
		//ADJUST 
		aScene = new Scene(aLayout, 200, 300);
		this.setScene(aScene);
	}
	
	private void createSelectionTool(ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		installTool(IconCreator.createSelectionIcon(), 
				ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("grabber.tooltip"), 
				null, true, pGroup, pGroupEx);
	}
	
	/*
	 * Adds a tool to the tool bars and menus.
	 * @param pIcon The icon for the tool
	 * @param pToolTip the tool's tool tip
	 * @param pTool the object representing the tool
	 * @param pIsSelected true if the tool is initially selected.
	 */
	private void installTool( Icon pIcon, String pToolTip, GraphElement pTool, boolean pIsSelected, ToggleGroup pCollapsed, ToggleGroup pExpanded )
	{
		final ToggleButton button = new ToggleButton(pToolTip+" replace");
		button.setStyle("-fx-focus-color: transparent;");
		// ADD GRAPHIC LATER
		
		// Sets the tooltip
		Tooltip toolTip = new Tooltip(pToolTip);
		Tooltip.install(button, toolTip);
		
		//Add button to toggle group and to the pane
		button.setToggleGroup(pCollapsed);
		aToolsLayout.getChildren().add(button);
		
		aButtons.add(button);
		button.setSelected(pIsSelected);
		aTools.add(pTool);
		
		
		final ToggleButton buttonEx = new ToggleButton(pToolTip+" replace");
		buttonEx.setStyle("-fx-focus-color: transparent;");
		
		// Sets the tooltip (move installs of tooltips later in code?)
		Tooltip.install(buttonEx, toolTip);

		// Add button to toggle group
		buttonEx.setToggleGroup(pExpanded);
		
		aButtonsEx.add(buttonEx);
	
		// Add button to VBox
		aToolsLayoutEx.getChildren().add(createExpandedRowElement(buttonEx, pToolTip));
		
		buttonEx.setSelected(pIsSelected);
      
		button.setOnAction(pEvent->
		{
			button.setSelected(true);
			buttonEx.setSelected(true);
		});
		buttonEx.setOnAction(pEvent->
		{
			button.setSelected(true);
			buttonEx.setSelected(true);
		});
		
		JMenuItem item = new JMenuItem(pToolTip, pIcon);
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				Platform.runLater(()->
				{
					button.setSelected(true);
					buttonEx.setSelected(true);
				});
			}
		});
		aPopupMenu.add(item);
	}
	
	/*
	 * Return a panel with a button on the left and a label on the right
	 */
	private Pane createExpandedRowElement(ButtonBase pButton, String pToolTip)
	{
		// Create HBox and add to VBox
		Label buttonLabel = new Label(pToolTip);
		HBox buttonLayout = new HBox();
		buttonLayout.getChildren().addAll(pButton, buttonLabel);
		// ADJUST FONT/BORDER
		
		return buttonLayout;
		
//		JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//		linePanel.add(pButton);
//		JLabel label = new JLabel(pToolTip);
//		Font font = new Font(label.getFont().getFontName(), Font.PLAIN, FONT_SIZE);
//		label.setFont(font);
//		label.setBorder(BorderFactory.createEmptyBorder(0, H_PADDING, 0, H_PADDING));
//		linePanel.add(label);
//		return linePanel;
	}
	
	private void createNodesAndEdgesTools(Graph pGraph, ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		ResourceBundle resources = ResourceBundle.getBundle(pGraph.getClass().getName() + "Strings");

		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			installTool(IconCreator.createIcon(nodeTypes[i]), resources.getString("node" + (i + 1) + ".tooltip"), 
			nodeTypes[i], false, pGroup, pGroupEx);
		}
		
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			installTool(IconCreator.createIcon(edgeTypes[i]), resources.getString("edge" + (i + 1) + ".tooltip"), 
					edgeTypes[i], false, pGroup, pGroupEx);
		}
	}
	
	/*
	 * Free up ctrl TAB for cycling windows
	 */
	private void freeCtrlTab()
	{
		Set<AWTKeyStroke> oldKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> newKeys = new HashSet<>();
		newKeys.addAll(oldKeys);
		newKeys.remove(KeyStroke.getKeyStroke("ctrl TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newKeys);
		oldKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		newKeys = new HashSet<>();
		newKeys.addAll(oldKeys);
		newKeys.remove(KeyStroke.getKeyStroke("ctrl shift TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newKeys); 
	}
	
	/**
     * Gets the node or edge prototype that is associated with
     * the currently selected button.
     * @return a Node or Edge prototype
	 */
	public GraphElement getSelectedTool()
	{
		return aTools.get(getSelectedButtonIndex());
	}
	
	/**
	 * Overrides the currently selected tool to be the grabber tool instead.
	 */
	public void setToolToBeSelect()
	{
		for( ToggleButton button : aButtons )
		{
			button.setSelected(false);
		}
		for( ToggleButton button : aButtonsEx )
		{
			button.setSelected(false);
		}
		aButtons.get(0).setSelected(true);
		aButtonsEx.get(0).setSelected(true);
	}

	private void addCopyToClipboard()
	{
		String imageLocation = getClass().getClassLoader().
				getResource(ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").
						getString("toolbar.copyToClipBoard")).toString();
		String toolTipText = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("file.copy_to_clipboard.text");
		
		final Button button = new Button();
		button.setStyle("-fx-focus-color: transparent;");
		button.setGraphic(new ImageView(imageLocation));
		Tooltip toolTip = new Tooltip(toolTipText);
		Tooltip.install(button, toolTip);
		if( aButtons.size() > 0 )
		{
			button.setPrefHeight(aButtons.get(0).getPrefHeight());
			button.setPrefWidth(aButtons.get(0).getPrefWidth());
		}
		aToolsLayout.getChildren().add(button);

		final Button buttonEx = new Button();
		buttonEx.setStyle("-fx-focus-color: transparent;");
		buttonEx.setGraphic(new ImageView(imageLocation));
		Tooltip.install(buttonEx, toolTip);

		aToolsLayoutEx.getChildren().add(createExpandedRowElement(buttonEx, toolTipText));
		
		if( aButtons.size() > 0 )
		{
			button.setPrefHeight(aButtons.get(0).getPrefHeight());
			button.setPrefWidth(aButtons.get(0).getPrefWidth());
			buttonEx.setPrefHeight(aButtons.get(0).getPrefHeight());
			buttonEx.setPrefWidth(aButtons.get(0).getPrefWidth());
		}

		button.setOnAction(pEvent-> copyToClipboard());
		buttonEx.setOnAction(pEvent-> copyToClipboard());
	}
	
	private void copyToClipboard()
	{
		// Obtain the editor frame by going through the component graph
		Container parent = getParent();
		while( parent.getClass() != EditorFrame.class )
		{
			parent = parent.getParent();
		}
		((EditorFrame)parent).copyToClipboard();
	}
		
	private void createExpandButton()
	{
		final Button expandButton = new Button(EXPAND);
		expandButton.setStyle("-fx-focus-color: transparent;");
		final Button collapseButton = new Button(COLLAPSE);
		collapseButton.setStyle("-fx-focus-color: transparent;");
		final String expandString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.expand");
		final String collapseString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.collapse");
		Tooltip expandToolTip = new Tooltip(expandString);
		Tooltip collapseToolTip = new Tooltip(collapseString);
		Tooltip.install(expandButton, expandToolTip);
		Tooltip.install(collapseButton, collapseToolTip);
		
		//ADJUST SIZES
		expandButton.setPrefHeight(BUTTON_SIZE);
		expandButton.setPrefWidth(BUTTON_SIZE);
		collapseButton.setPrefHeight(BUTTON_SIZE);
		collapseButton.setPrefWidth(BUTTON_SIZE);
		
		expandButton.setOnAction(pEvent ->
		{
			synchronizeToolSelection();
			aToolPanelEx.setBounds(aToolPanel.getBounds());
			remove(aToolPanel);
			add(aToolPanelEx, BorderLayout.CENTER);
			aScene.setRoot(aLayoutEx);
			
		});
		collapseButton.setOnAction(pEvent -> 
		{
			synchronizeToolSelection();
			aToolPanel.setBounds(aToolPanelEx.getBounds());
			remove(aToolPanelEx);
			add(aToolPanel, BorderLayout.CENTER);
			aScene.setRoot(aLayout);
		});
		
		// adds buttons to layout
		aLayout.setBottom(expandButton);
		aLayoutEx.setBottom(collapseButton);
	}
	
	private void synchronizeToolSelection()
	{
		int index = getSelectedButtonIndex();
		assert index >= 0;
		aButtons.get(index).setSelected(true);
		aButtonsEx.get(index).setSelected(true);
	}
	
	private int getSelectedButtonIndex()
	{
		ArrayList<ToggleButton> activeButtons = aButtons;
		if( isExpanded() )
		{
			activeButtons = aButtonsEx;
		}
		
		for(int i = 0; i < activeButtons.size(); i++)
		{
			ToggleButton button = activeButtons.get(i);
			if(button.isSelected())
			{
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * The toolbar is expanded iff the main panel contains
	 * the expanded toolbar as one of its components.
	 */
	private boolean isExpanded()
	{
		for( Component component : getComponents() )
		{
			if( component == aToolPanelEx )
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Show the pop-up menu corresponding to this toolbar.
	 * @param pPanel The panel associated with this menu.
	 * @param pPoint The point where to show the menu.
	 */
	public void showPopup(GraphPanel pPanel, Point pPoint) 
	{
		aPopupMenu.show(pPanel, pPoint.getX(), pPoint.getY());
	}
}