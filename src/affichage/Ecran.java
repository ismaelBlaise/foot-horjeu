package affichage;

import javax.swing.*;
import java.awt.*;

public class Ecran extends JPanel {
    private Image image;
    private String imagePath;  

    
    public void loadImage(String imagePath) {
        this.imagePath = imagePath; 
        image = new ImageIcon(imagePath).getImage();
        repaint();
    }

    
    public void clearImage() {
        image = null;
        imagePath = null; 
        repaint();
    }

    
    public String getCurrentImagePath() {
        return imagePath;
    }

    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);

    //     if (image != null) {
    //         Graphics2D g2d = (Graphics2D) g;

    //         int panelWidth = getWidth();
    //         int panelHeight = getHeight();

    //         int imageWidth = image.getWidth(this);
    //         int imageHeight = image.getHeight(this);

    //         // Calcul de l'échelle pour que l'image rentre dans le panel tout en respectant les proportions
    //         double scale = Math.min((double) panelWidth / imageWidth, (double) panelHeight / imageHeight);

    //         // Calcul des dimensions finales de l'image redimensionnée
    //         int drawWidth = (int) (imageWidth * scale);
    //         int drawHeight = (int) (imageHeight * scale);

    //         // Calcul de la position pour centrer l'image
    //         int x = (panelWidth - drawWidth) / 2;
    //         int y = (panelHeight - drawHeight) / 2;

    //         // Dessiner l'image redimensionnée et centrée
    //         g2d.drawImage(image, x, y, drawWidth, drawHeight, this);
    //     } else {
    //         // Message par défaut lorsqu'aucune image n'est chargée
    //         g.setFont(new Font("Arial", Font.BOLD, 20));
    //         g.setColor(Color.GRAY);
    //         String message = "Aucune image chargée";
    //         FontMetrics metrics = g.getFontMetrics();
    //         int x = (getWidth() - metrics.stringWidth(message)) / 2;
    //         int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
    //         g.drawString(message, x, y);
    //     }
    // }

    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);

    //     if (image != null) {
    //         Graphics2D g2d = (Graphics2D) g;

    //         int panelWidth = getWidth();
    //         int panelHeight = getHeight();

    //         // Convertir Image en BufferedImage si nécessaire
    //         BufferedImage bufferedImage = new BufferedImage(image.getWidth(this), image.getHeight(this), BufferedImage.TYPE_INT_ARGB);
    //         Graphics2D bufferedGraphics = bufferedImage.createGraphics();
    //         bufferedGraphics.drawImage(image, 0, 0, null);
    //         bufferedGraphics.dispose();

    //         // Dimensions de l'image d'origine
    //         int imageWidth = bufferedImage.getWidth();
    //         int imageHeight = bufferedImage.getHeight();

    //         // Calcul de l'échelle pour que l'image tienne verticalement tout en respectant les proportions
    //         double scale = Math.min((double) panelWidth / imageHeight, (double) panelHeight / imageWidth);

    //         // Calcul des dimensions après rotation
    //         int drawWidth = (int) (imageHeight * scale);
    //         int drawHeight = (int) (imageWidth * scale);

    //         // Position pour centrer l'image
    //         int x = (panelWidth - drawWidth) / 2;
    //         int y = (panelHeight - drawHeight) / 2;

    //         // Créer une transformation pour la rotation
    //         AffineTransform transform = new AffineTransform();
    //         transform.translate(x + drawWidth / 2.0, y + drawHeight / 2.0);
    //         transform.rotate(Math.toRadians(90));
    //         transform.scale(scale, scale);
    //         transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

    //         // Appliquer la transformation et dessiner l'image
    //         g2d.drawImage(bufferedImage, transform, this);
    //     } else {
    //         // Message par défaut lorsqu'aucune image n'est chargée
    //         g.setFont(new Font("Arial", Font.BOLD, 20));
    //         g.setColor(Color.GRAY);
    //         String message = "Aucune image chargée";
    //         FontMetrics metrics = g.getFontMetrics();
    //         int x = (getWidth() - metrics.stringWidth(message)) / 2;
    //         int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
    //         g.drawString(message, x, y);
    //     }
    // }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

           
            double panelAspect = (double) panelWidth / panelHeight;
            double imageAspect = (double) imageWidth / imageHeight;

            int drawWidth, drawHeight;

            if (imageAspect > panelAspect) {
               
                drawWidth = panelWidth;
                drawHeight = (int) (panelWidth / imageAspect);
            } else {
                
                drawHeight = panelHeight;
                drawWidth = (int) (panelHeight * imageAspect);
            }

           
            int x = (panelWidth - drawWidth) / 2;
            int y = (panelHeight - drawHeight) / 2;

            g.drawImage(image, x, y, drawWidth, drawHeight, this);
        } else {
            
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.GRAY);
            String message = "Aucune image chargée";
            FontMetrics metrics = g.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(message)) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(message, x, y);
        }
    }
}
