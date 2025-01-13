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
