/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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

import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;


/**
 * A class for creating menus from strings in a 
 * resource bundle.
 * 
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
class MenuFactory
{
	private ResourceBundle aBundle;
	private final String aSystem; 
	
	/**
	 * @param pBundle The bundle to use to fetch
	 * resources.
	 */
	MenuFactory(ResourceBundle pBundle)
	{
		aBundle = pBundle;
		aSystem = System.getProperty("os.name").toLowerCase();
	}

	/**
	 * Creates a menu item that calls a method in response to the action event.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pTarget The object on which pMethodName will be invoked when the menu is selected.
	 * @param pMethodName The method to invoke when the menu is selected.
	 * @return A menu item for the action described.
	 */
	public JMenuItem createJMenuItem(String pPrefix, Object pTarget, String pMethodName)
	{
		return createJMenuItem(pPrefix, java.beans.EventHandler.create(ActionListener.class, pTarget, pMethodName));
	}

	/**
	 * Creates a menu item where pListener is triggered when the menu item is selected.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pListener The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public JMenuItem createJMenuItem(String pPrefix, ActionListener pListener)
	{
		String text = aBundle.getString(pPrefix + ".text");
		JMenuItem menuItem = new JMenuItem(text);
		return configureJMenuItem(menuItem, pPrefix, pListener);
	}
	
	/**
	 * Creates a menu item where pListener is triggered when the menu item is selected.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pHandler The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public MenuItem createMenuItem(String pPrefix, EventHandler<ActionEvent> pHandler)
	{
		MenuItem menuItem = new MenuItem();
		return configureMenuItem(menuItem, pPrefix, pHandler);
	}
	
	/**
	 * Create a checkbox menu.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pHandler The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public MenuItem createCheckMenuItem(String pPrefix, EventHandler<ActionEvent> pHandler)
	{
		MenuItem menuItem = new CheckMenuItem();
		return configureMenuItem(menuItem, pPrefix, pHandler);
	}	

	/*
	 * Configures the menu with text, mnemonic, accelerator, etc
	 */
	private JMenuItem configureJMenuItem(JMenuItem pMenuItem, String pPrefix, ActionListener pListener)
	{
		pMenuItem.addActionListener(pListener);
		return pMenuItem;
	}

	/*
	 * Configures the menu with text, mnemonic, accelerator, etc
	 */
	private MenuItem configureMenuItem(MenuItem pMenuItem, String pPrefix, EventHandler<ActionEvent> pHandler)
	{
		pMenuItem.setOnAction(pHandler);
		String text = aBundle.getString(pPrefix + ".text");
		
		if( aBundle.containsKey(pPrefix + ".mnemonic"))
		{
			// get index of character to properly insert mnemonic symbol "_"
			int index = text.indexOf(aBundle.getString(pPrefix + ".mnemonic").charAt(0));
			if(index < 0) 
			{
				index = text.indexOf(aBundle.getString(pPrefix + ".mnemonic").toLowerCase().charAt(0));
			}
			
			if (index >= 0) 
			{
				text = text.substring(0, index) + "_" + text.substring(index);
			}
			else 
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination("ALT+"+aBundle.getString(pPrefix + ".mnemonic")));
			}
		}
		pMenuItem.setText(text);
		
		if( aBundle.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(aSystem.indexOf("mac") >= 0)
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.win")));
			}
			
		}
		else if( aBundle.containsKey(pPrefix + ".accelerator.all")) 
		{
			pMenuItem.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.all")));	
		}
		if( aBundle.containsKey(pPrefix + ".icon"))
		{
			pMenuItem.setGraphic(new ImageView(getClass().getClassLoader().getResource(aBundle.getString(pPrefix + ".icon")).toString()));
		}
		return pMenuItem;
	}

	
	/**
	 * Create a menu that corresponds to the resource for key pPrefix.
	 * @param pPrefix A string such as "file" that indicates the menu->submenu path
	 * @return A configured menu
	 */
	public JMenu createJMenu(String pPrefix)
	{
		String text = aBundle.getString(pPrefix + ".text");
		JMenu menu = new JMenu(text);
		return menu;
	}
	
	/**
	 * Create a menu that corresponds to the resource for key pPrefix.
	 * @param pPrefix A string such as "file" that indicates the menu->submenu path
	 * @return A configured menu
	 */
	public Menu createMenu(String pPrefix)
	{
		String text = aBundle.getString(pPrefix + ".text");
		Menu menu = new Menu();
		if( aBundle.containsKey(pPrefix + ".mnemonic"))
		{
			int index = text.indexOf(aBundle.getString(pPrefix + ".mnemonic").charAt(0));
			assert index >= 0;
			text = text.substring(0, index) + "_" + text.substring(index);
		}
		menu.setText(text);
	
		if( aBundle.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(aSystem.indexOf("mac") >= 0)
			{
				menu.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				menu.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.win")));
			}
		}
		else if( aBundle.containsKey(pPrefix + ".accelerator.all")) 
		{
			menu.setAccelerator(KeyCombination.keyCombination(aBundle.getString(pPrefix + ".accelerator.all")));	
		}
		if( aBundle.containsKey(pPrefix + ".icon"))
		{
			menu.setGraphic(new ImageView(getClass().getClassLoader().getResource(aBundle.getString(pPrefix + ".icon")).toString()));
		}

		return menu;
	}
}
