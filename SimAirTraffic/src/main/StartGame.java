package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import config.GameConfiguration;
import gui.MainGUI;

/*
 * The opening window of the software.
 * Allows the user to start a new game, load a saved game, or access the game guide.
 * @author MANSOUR Ines
*/
public class StartGame extends JFrame {
    private JPanel panel;
    private JButton boutonCommencer;
    private BufferedImage image;
    private Color color = new ColorUIResource(190, 124, 124);
    
    /**
     * Creates and initializes a new DebutJeu object.
     */
    public StartGame() {
        setTitle("Air Traffic Control");
        createPanel();
        getContentPane().add(panel);
        //loadAndPlayBackgroundMusic();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(image.getWidth(), image.getHeight());
        setLocationRelativeTo(null);
        setVisible(true);
        
    }

    /**
     * Creates the panel of the DebutJeu object.
     */
    private void createPanel() {
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(color);

        loadImage();
        createStartButton();
        //createLoadGameButton();
        createGameInfoButton();
    }

    /**
     * Loads the image for the DebutJeu object.
     */
    private void loadImage() {
        try {
            image = ImageIO.read(new File(GameConfiguration.FRAME_DEBUT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the button to start a new game.
     */
    private void createStartButton() {
        boutonCommencer = new JButton("Début du jeu");
        boutonCommencer.setBounds(300, 250, 200, 30);
        boutonCommencer.setBackground(color);
        boutonCommencer.addActionListener(e -> {
            String input = showInputDialog();
            if (input != null) {
                dispose(); // Ferme la fenêtre de début de jeu
                MainGUI mainGui = new MainGUI("My Home Decoration");
                //mainGui.getInformationPanel().displayWelcomeMessage(input);
                mainGui.setVisible(true);
            }
        });
        panel.add(boutonCommencer);
    }
    
    /**
     * Displays a dialog for the user to enter their username.
     * 
     * @return the entered username or null if no username was entered.
     */
    private String showInputDialog() {
        JTextField userNameField = new JTextField(10);
        JLabel label = new JLabel("Nom d'utilisateur :");
        label.setForeground(Color.WHITE);
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(color);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(userNameField, BorderLayout.CENTER);
    
        UIManager.put("OptionPane.background", color);
        UIManager.put("Panel.background", color);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Button.background",Color.WHITE );
        UIManager.put("Button.foreground", Color.BLACK);
    
        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Entrez votre nom d'utilisateur",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            String userName = userNameField.getText().trim();
            if (!userName.isEmpty()) {
                return userName;
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez entrer un nom d'utilisateur valide.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return showInputDialog();
            }
        }
    
        return null;
    }
    

    /**
     * Creates the button to load a saved game.
     */
    
    /**
    private void createLoadGameButton() {
        JButton boutonAnciennesSauvegardes = new JButton("Dernière partie");
        boutonAnciennesSauvegardes.setBounds(300, 280, 200, 30);
        boutonAnciennesSauvegardes.setBackground(color);
        boutonAnciennesSauvegardes.addActionListener(e -> {
        	//ExtractSavedGame extractor = new ExtractSavedGame("src/dashboard.ser");
    		
    		Dashboard dash = extractor.getDash();
    		
    		if(dash == null) {
    			JOptionPane.showMessageDialog(null, "Aucune sauvegarde trouvée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
    		}
    		
    		dash.mouseAddDash();
    		
    		MainGui mainGui = new MainGui("jeu",dash);
    		    		
            mainGui.setVisible(true);
            dispose();
        });
        panel.add(boutonAnciennesSauvegardes);
    }
    **/
    
    
    /**
     * Creates the button to access the game guide.
    */
    private void createGameInfoButton() {
        JButton boutonInformations = new JButton("Guide");
        boutonInformations.setBounds(300, 310, 200, 30);
        boutonInformations.setBackground(color);
        boutonInformations.addActionListener(e -> {
            showGameInfo();
        });
        panel.add(boutonInformations);
    }

    /**
     * Overrides the paint method to display the image.
     *  @param g the Graphics object
    */
    public void paint(Graphics g) {
        // Affichage de l'image
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    /*
     *  Displays the game guide in a new JFrame.
    */
    private void showGameInfo() {
        JFrame infoFrame = new JFrame("Guide");
        infoFrame.setPreferredSize(new Dimension(800, 500));
        infoFrame.setLayout(new BorderLayout());
        infoFrame.setBackground(Color.WHITE);
        
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Bienvenue dans My Home Decoration !");
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        titleLabel.setForeground(new Color(55, 73, 85));
        titlePanel.add(titleLabel);
        titlePanel.setBackground(color);
        
        JTextArea infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
        infoTextArea.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
        infoTextArea.setForeground(new Color(55, 73, 85));
        infoTextArea.setText(
                " My Home Decoration est un logiciel de conception et de modélisation d'appartement.\n\n" +
                " Pour créer votre appartement, suivez les étapes suivantes :\n\n" +
                " 1. Choisissez le style de votre choix pour votre appartement.\n" +
                " 2. Tracez le plan de chacune des pièces de votre appartement en sélectionnant les cases de la grille.\n" +
                " 3. Pour une pièce créée, déclarez-la ensuite en cliquant sur 'Déclarer une pièce'.\n" +
                " 4. Placez les meubles dans votre appartement en utilisant les boutons à disposition sur le panel droit.\n" +
                " 5. Sauvegardez votre appartement en cliquant sur 'Sauvegarder'.\n\n" +
                " My Home Décoration offre également d'autres fonctionnalités pour vous aider à concevoir l'appartement de vos rêves :\n\n" +
                " - Génération automatique de votre décoration d'intérieur en utilisant l'option 'Générer automatiquement'.\n" +
                " - Consultation d'exemples pour vous inspirer en utilisant l'option 'Exemples'.\n" +
                " - Option de retour en arrière pour annuler les actions précédentes en utilisant l'option 'Annuler'.\n"
        );
        
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
       // closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(55, 73, 85));
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> {
            infoFrame.dispose();
        });
        buttonPanel.add(closeButton);
        buttonPanel.setBackground( color);
        
        infoFrame.add(titlePanel, BorderLayout.NORTH);
        infoFrame.add(scrollPane, BorderLayout.CENTER);
        infoFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        infoFrame.pack();
        infoFrame.setLocationRelativeTo(null);
        infoFrame.setVisible(true);
    }
    
            
}
