package ca.mcgill.cs.jetuml;

import org.junit.BeforeClass;

import javafx.embed.swing.JFXPanel;

public abstract class JavaFXTest
{
    @BeforeClass
    public static void initToolkit()	// initializes JavaFX toolkit and environment
    {
    	new JFXPanel();
    }
}