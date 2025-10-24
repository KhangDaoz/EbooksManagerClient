package com.ebookmanager.model.InteractClasses;
import javax.swing.*;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Dimension;
/*
 * jFrame
 * Jpanel
 * LayourManager
 * jButton,
 * JMenuItem
 * AactionListener
 */
public class ReadingGUI extends JFrame{

    private JScrollPane pdfJScrollPane;
    private ImagePanel pdfPanel;
    private JLabel pageLabel;
    
    // Book managing: render page, count number of page
    private pdfManager pdfManager;
    private int currentPage = 0;
    // Class uses to paint Book's page
    /*
     * Attribute is a image class ~~ graphic version of image
     * paintComponent: reset image content.
     */
    private static class ImagePanel extends JPanel{
        private BufferedImage image;

        //Zoom variable
        private double scale = 1.0;

        public void setImage(BufferedImage image)
        {
            this.image = image;
            this.scale = 1.0;
            revalidateAndRepaint();
        }
        public void setScale(double var)
        {
            this.scale = Math.max(0.2, Math.min(var, 5.0));
            revalidateAndRepaint();
        }
        public double getScale()
        {
            return scale;
        }

        @Override
        public Dimension getPreferredSize()
        {
            if(image == null)
            {
                return super.getPreferredSize();
            }

            int width = (int) (image.getWidth() * scale);
            int height = (int) (image.getHeight() * scale);
            return new Dimension(width, height);
        }
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            if(image != null)
            {
                Graphics2D g2d = (Graphics2D) g;
                int panelWidth = this.getWidth();
                int panelHeight = this.getHeight();

                int imageWidth = (int) (image.getWidth()*scale);
                int imageHeight = (int) (image.getHeight()*scale);
                // Resize image

                int x = (panelWidth - imageWidth)/2;
                int y = (panelHeight - imageHeight)/2;
                g2d.drawImage(image, x, y, imageWidth, imageHeight, null);
            }
        }
        private void revalidateAndRepaint()
        {
            revalidate();
            repaint();
        }

        // Fit Page to Window

        public void fitToWindow()
        {
            if(image == null) return;

            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            if(panelWidth > 0)
            {
                double widthScale = (double) panelWidth/image.getWidth();
                double heightScale = (double) panelHeight/image.getHeight();
                setScale(Math.min(widthScale, heightScale));
            }
        }
    }

    public ReadingGUI()
    {
        // Main window
        setTitle("Book Reader");
        
        // Size of window
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screenSize = tool.getScreenSize();
        setSize((int)screenSize.getWidth(), (int)screenSize.getHeight()-50);

        // place window in centre of screen
        setLocationRelativeTo(null); 
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        
        setupMenuBar();

        pdfPanel = new ImagePanel();
        
        pdfPanel.setBackground(Color.CYAN);
        // Add Scroll panel
        pdfJScrollPane = new JScrollPane(pdfPanel);

        pdfJScrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if(!pdfPanel.isShowing())
                    return;
                if(e.isControlDown())
                {                
                    int rotation = e.getWheelRotation();
                    double newScale = pdfPanel.getScale() - (rotation*0.1);

                    pdfPanel.setScale(newScale);
                }
            }
        });
        add(pdfJScrollPane, BorderLayout.CENTER);

        setupControlPanel();
    }

    public void openFileAction(ActionEvent e)
    {
        JFileChooser chooser = new JFileChooser();
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            if(file.getName().toLowerCase().endsWith(".pdf"))
            {
                openPdf(file);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Invalid File");
            }
        }
    }
    public void openPdf(File file)
    {
        try 
        {
            if(pdfManager != null)
            {
                pdfManager.close();
            }
            
            pdfManager = new pdfManager(file);
            currentPage = 0;
            displayPage(currentPage);
            setTitle(file.getName());
        } 
        catch (IOException e) {
            
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid File", 
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void setupControlPanel()
    {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("Prev");
        prevButton.addActionListener(this::PrevPage);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(this::NextPage);
        pageLabel = new JLabel("0/0");

        controlPanel.add(prevButton);
        controlPanel.add(pageLabel);
        controlPanel.add(nextButton);

        add(controlPanel, BorderLayout.SOUTH);

    }
    public void PrevPage(ActionEvent e)
    {
        if(pdfManager != null 
        && currentPage > 0)
        {
            displayPage(--currentPage);
        }
    }
    public void NextPage(ActionEvent e)
    {
        if(pdfManager != null 
        && currentPage < pdfManager.getPageCount()-1)
        {
            displayPage(++currentPage);
        }
    }

    public void displayPage(int page)
    {
        if(pdfManager == null) return;
        try 
        {
            BufferedImage pageImage = pdfManager.renderPage(page);
            pdfPanel.setImage(pageImage);
            pdfPanel.fitToWindow();
            pageLabel.setText(String.format("Page: %d/%d", 
            page, pdfManager.getPageCount()));
        } catch (Exception e) {
        
            e.printStackTrace();
        }
    }
 
    public void setupMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem =  new JMenuItem("Open File ...");
        openMenuItem.addActionListener(this::openFileAction);
        fileMenu.add(openMenuItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }
    public static void main(String args[])
    {
        SwingUtilities.invokeLater(() -> 
        {
            ReadingGUI reader = new ReadingGUI();
            reader.setVisible(true);
        }
        );
    }
}
