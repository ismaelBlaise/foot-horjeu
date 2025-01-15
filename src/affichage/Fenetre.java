package affichage;

import javax.swing.*;

import util.Joueur;

import java.awt.*;
import java.io.File;
import java.util.List;

public class Fenetre extends JFrame {
    private Ecran imagePanel;
    private Historique historyManager;
    private DefaultListModel<String> historyListModel;

    public Fenetre() {
        setTitle("Visionneuse d'hors jeu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialiser les composants
        historyManager = new Historique();
        imagePanel = new Ecran();

        // Menu
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Historique
        historyListModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyListModel);
        JScrollPane historyScrollPane = new JScrollPane(historyList);

        // Menu contextuel pour renommer
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Renommer");
        renameItem.addActionListener(e -> renameHistoryEntry(historyList));
        contextMenu.add(renameItem);

        historyList.setComponentPopupMenu(contextMenu);

        // Sélectionner une image depuis l'historique
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && historyList.getSelectedValue() != null) {
                String selectedImagePath = historyList.getSelectedValue();
                imagePanel.loadImage(selectedImagePath);
            }
        });

        // Mise en page
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, historyScrollPane, imagePanel);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem importItem = new JMenuItem("Importer une image");
        JMenuItem clearItem = new JMenuItem("Effacer l'image");
        JMenuItem verifyItem = new JMenuItem("Vérifier"); // Nouveau menu Vérifier
        JMenuItem exitItem = new JMenuItem("Quitter");

        importItem.addActionListener(e -> importImage());
        clearItem.addActionListener(e -> imagePanel.clearImage());
        verifyItem.addActionListener(e -> verifyImage());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(verifyItem); // Ajout au menu
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    private void importImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            imagePanel.loadImage(imagePath);
            historyManager.addToHistory(imagePath);
            updateHistoryList();
        }
    }

    private void verifyImage() {
        String currentImage = imagePanel.getCurrentImagePath();
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this, "Aucune image chargée.", "Vérification", JOptionPane.WARNING_MESSAGE);
            return;
        }

         
        fonction.AnalyseImage analyse = new fonction.AnalyseImage(currentImage);

        try {
            @SuppressWarnings("unused")
            List<Joueur> resultats = analyse.detectHorsJeu();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } 
        
         
        

         
        File imageFile = new File(currentImage);
        if (imageFile.exists()) {
             
            imagePanel.loadImage("image_modifiee.png");

            
        } else {
            JOptionPane.showMessageDialog(this, "L'image actuelle n'existe plus.", "Vérification", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void renameHistoryEntry(JList<String> historyList) {
        String selectedEntry = historyList.getSelectedValue();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(this, "Aucune entrée sélectionnée.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Entrez un nouveau nom :", "Renommer", JOptionPane.PLAIN_MESSAGE);
        if (newName != null && !newName.trim().isEmpty()) {
            historyManager.renameHistoryEntry(selectedEntry, newName);
            updateHistoryList();
        }
    }

    private void updateHistoryList() {
        historyListModel.clear();
        List<String> history = historyManager.getHistory();
        for (String path : history) {
            historyListModel.addElement(path);
        }
    }
}
