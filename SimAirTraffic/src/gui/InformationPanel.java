package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InformationPanel extends JPanel{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea messageArea;

    /**
     * Constructor to initialize the information panel.
     */
    public InformationPanel() {
        super(new BorderLayout());

        // Ajouter un label pour le message de bienvenue
        JLabel welcomeLabel = new JLabel();
        welcomeLabel.setText("Bienvenue dans AirTraffic Control !");
        welcomeLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(55, 73, 85));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(0, 100));

        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This method adds a message to the information panel.
     * 
     * @param message The message to be displayed on the panel.
     */
    public void addMessage(String message) {
        messageArea.append(message + "\n");
    }

}
