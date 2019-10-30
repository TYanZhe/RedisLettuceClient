package cn.org.tpeach.nosql.view;

import bsh.util.GUIConsoleInterface;
import bsh.util.JConsole;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/** 
 * Example of using the BeanShell project's JConsole in
 * your own application.
 * 
 * JConsole is a command line input console that has support 
 * for command history, cut/copy/paste, a blinking cursor, 
 * command completion, Unicode character input, coloured text 
 * output and comes wrapped in a scroll pane.
 * 
 * For more info, see http://www.beanshell.org/manual/jconsole.html
 * 
 * @author tukushan
 */
public class JConsoleExample {

    public static void main(String[] args) {

        //define a frame and add a console to it
        JFrame frame = new JFrame("JConsole example");

        JConsole console = new JConsole();

        frame.getContentPane().add(console);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,400);

        frame.setVisible(true);

        inputLoop(console, "JCE (type 'quit' to exit): ");

        System.exit(0);
    }

    /**
     * Print prompt and echos commands entered via the JConsole
     * 
     * @param console a GUIConsoleInterface which in addition to 
     *         basic input and output also provides coloured text
     *         output and name completion
     * @param prompt text to display before each input line
     */
    private static void inputLoop(GUIConsoleInterface console, String prompt) {
        Reader input = console.getIn();
        BufferedReader bufInput = new BufferedReader(input);

        String newline = System.getProperty("line.separator");

        console.print(prompt, Color.BLUE);

        String line;
        try {
            while ((line = bufInput.readLine()) != null) {
                console.print("You typed: " + line + newline, Color.ORANGE);

                // try to sync up the console
                //System.out.flush();
                //System.err.flush();
                //Thread.yield();  // this helps a little

                if (line.equals("quit")) break; 
                console.print(prompt, Color.BLUE);
            }
            bufInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}