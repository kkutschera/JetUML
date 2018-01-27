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

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.IconCreator;
import ca.mcgill.cs.jetuml.views.ImageCreator;


/**
 *  A collapsible tool bar than contains various tools and optional
 *  command shortcut buttons. Only one tool can be selected at the time.
 *  The tool bar also controls a pop-up menu with the same tools as 
 *  the tool bar.
 *  
 *  @author Martin P. Robillard
 */

// CREATE A VERTICAL LAYOUT IN FX (anchor, flow??)

@SuppressWarnings("serial")
public class ToolBar extends JFXPanel
{
//	private static final int BUTTON_SIZE = 25;
	private static final int BUTTON_SIZE = 50;
	private static final int H_PADDING = 5;
	private static final int V_PADDING = 5;
	private static final int FONT_SIZE = 20;
	private static final String EXPAND = "<<";
	private static final String COLLAPSE = ">>";
	
	private ArrayList<ToggleButton> aButtons = new ArrayList<>();
	private ArrayList<ToggleButton> aButtonsEx = new ArrayList<>();
	
	private BorderPane aLayout = new BorderPane();
	private BorderPane aLayoutEx = new BorderPane();
	
	private VBox aToolsLayout = new VBox(V_PADDING);
	private VBox aToolsLayoutEx = new VBox(V_PADDING);
	
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
		
		aToolsLayout.setPadding(new Insets(V_PADDING, H_PADDING, V_PADDING, H_PADDING));
		aLayout.setCenter(aToolsLayout);
		
		aToolsLayoutEx.setPadding(new Insets(V_PADDING, H_PADDING, V_PADDING, H_PADDING));
		aLayoutEx.setCenter(aToolsLayoutEx);
		
		createSelectionTool(group, groupEx);
		createNodesAndEdgesTools(pGraph, group, groupEx);
		
		addCopyToClipboard();
		createExpandButton();
		
		//ADJUST 
		aScene = new Scene(aLayout);
		this.setScene(aScene);
		Scene aSceneEx = new Scene(aLayoutEx,500, 500);
		this.setScene(aSceneEx);
	}
	
	private void createSelectionTool(ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		installTool(ImageCreator.createSelectionImage(), IconCreator.createSelectionIcon(), 
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
	private void installTool(Image pImage, 
							 Icon pIcon, 
							 String pToolTip, 
							 GraphElement pTool, 
							 boolean pIsSelected, 
							 ToggleGroup pCollapsed, 
							 ToggleGroup pExpanded)
	{
		final ToggleButton button = new ToggleButton();
		button.setGraphic(new ImageView(pImage));
		button.setToggleGroup(pCollapsed);
		aButtons.add(button);
		button.setSelected(pIsSelected);
		aTools.add(pTool);
		aToolsLayout.getChildren().add(button);
		
		final ToggleButton buttonEx = new ToggleButton();
		buttonEx.setGraphic(new ImageView(pImage));
		buttonEx.setToggleGroup(pExpanded);		
		aButtonsEx.add(buttonEx);
		buttonEx.setSelected(pIsSelected);
		aToolsLayoutEx.getChildren().add(createExpandedRowElement(buttonEx, pToolTip));
	
		Tooltip toolTip = new Tooltip(pToolTip);
		Tooltip.install(button, toolTip);
		Tooltip.install(buttonEx, toolTip);

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
	 * Return a pane with a button on the left and a label on the right
	 */
	private Pane createExpandedRowElement(ButtonBase pButton, String pToolTip)
	{
		Label buttonLabel = new Label(pToolTip);
		Font font = new Font(buttonLabel.getFont().getName(), FONT_SIZE);
		buttonLabel.setFont(font);
		HBox buttonLayout = new HBox();
		buttonLayout.getChildren().addAll(pButton, buttonLabel);
		return buttonLayout;
	}
	
	private void createNodesAndEdgesTools(Graph pGraph, ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		ResourceBundle resources = ResourceBundle.getBundle(pGraph.getClass().getName() + "Strings");

		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			installTool(ImageCreator.createImage(nodeTypes[i]), IconCreator.createIcon(nodeTypes[i]), 
					resources.getString("node" + (i + 1) + ".tooltip"), nodeTypes[i], false, pGroup, pGroupEx);
		}
		
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			installTool(ImageCreator.createImage(edgeTypes[i]), IconCreator.createIcon(edgeTypes[i]), 
					resources.getString("edge" + (i + 1) + ".tooltip"), edgeTypes[i], false, pGroup, pGroupEx);
		}
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
		final Button collapseButton = new Button(COLLAPSE);
		final String expandString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.expand");
		final String collapseString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.collapse");
		Tooltip expandToolTip = new Tooltip(expandString);
		Tooltip collapseToolTip = new Tooltip(collapseString);
		Tooltip.install(expandButton, expandToolTip);
		Tooltip.install(collapseButton, collapseToolTip);
		
		expandButton.setPrefHeight(BUTTON_SIZE);
		expandButton.setPrefWidth(BUTTON_SIZE);
		collapseButton.setPrefHeight(BUTTON_SIZE);
		collapseButton.setPrefWidth(BUTTON_SIZE);
		
		expandButton.setOnAction(pEvent ->
		{
			synchronizeToolSelection();
			aScene.setRoot(aLayoutEx);
			SwingUtilities.invokeLater(() -> this.setPreferredSize(this.getPreferredSize()));
		});
		collapseButton.setOnAction(pEvent -> 
		{
			synchronizeToolSelection();
			aScene.setRoot(aLayout);
		});
		
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
		if(aScene.getRoot() == aLayoutEx) 
		{
			return true;
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