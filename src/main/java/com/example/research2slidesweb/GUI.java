package com.example.research2slidesweb;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.springframework.web.multipart.MultipartFile;

public class GUI implements ActionListener{
	final JFileChooser fc = new JFileChooser();
	final JFileChooser fc2 = new JFileChooser();
	private JPanel cardPanel;
    private CardLayout cardLayout;
    
    boolean nextListenerAdded = false;
    boolean generateListenerAdded = false;
    JFrame frame = new JFrame("Research2Slides");
    
    JButton selectButton = new JButton("Select a file");
    JButton destinationButton = new JButton("Select a directory");
	JButton generateButton = new JButton("Generate PPT");
    JButton switchToScreen1 = new JButton("Back");
    JButton switchToScreen2 = new JButton("Next");
    
    JLabel fileLabel = new JLabel("No File Selected");
    JLabel generateSuccess = new JLabel("PPT generated successfully!");
    JLabel downloadLabel = new JLabel("No File Selected");
    
	JPanel panelBody = new JPanel();
	JPanel panelBodyLower = new JPanel();
	JPanel screen1 = createMainScreen("Research2Slides");
    JPanel screen2 = createScreen("Research2Slides");
	
    Border borderCommon = BorderFactory.createEmptyBorder(0, 10, 10, 10);
    Border borderBodyLower = BorderFactory.createEmptyBorder(70, 10, 10, 10);
    
    Color lightGray = Color.decode("#F2F2F2");
    Color buttonColor = Color.GREEN;
	//Color buttonColor = Color.decode("#20C997");
    Color textColor = Color.BLACK;
    
    public GUI() {
    	fc.addChoosableFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
    	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add the screens to the cardPanel
        cardPanel.add(screen1, "SCREEN1");
        cardPanel.add(screen2, "SCREEN2");
        
		BoxLayout layoutBodyLower = new BoxLayout(panelBodyLower, BoxLayout.PAGE_AXIS);
		      
        panelBodyLower.setLayout(layoutBodyLower);
        panelBodyLower.setBorder(borderBodyLower);
		
        //----------------------COLOUR STUFF--------------
        frame.getContentPane().setBackground(lightGray);
        selectButton.setBackground(buttonColor);
        selectButton.setForeground(textColor);
        generateButton.setBackground(buttonColor);
        generateButton.setForeground(textColor);
        destinationButton.setBackground(buttonColor);
        destinationButton.setForeground(textColor);
        
        frame.getContentPane().setBackground(lightGray);
        switchToScreen1.setBackground(buttonColor);
        switchToScreen1.setForeground(textColor);
        switchToScreen2.setBackground(buttonColor);
        switchToScreen2.setForeground(textColor);    
		//-------------------------------------------------
		
        //Back button ActionListener
        switchToScreen1.addActionListener(this);
             
        // Add buttons to a panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(switchToScreen1);
        buttonPanel.add(switchToScreen2);
        
        // Add the buttonPanel to the SOUTH position
        frame.add(cardPanel, BorderLayout.NORTH);
        frame.add(Box.createRigidArea(new Dimension(0, 25)), BorderLayout.CENTER); 
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
    private JPanel createMainScreen(String title) {
    	BoxLayout layoutBody = new BoxLayout(panelBody, BoxLayout.PAGE_AXIS);
        
        Color titleTextColor = Color.decode("#333333");
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog",Font.PLAIN,72));
		titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        titleLabel.setForeground(titleTextColor);
    	
    	selectButton.addActionListener(this);
		selectButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
		
		fileLabel.setAlignmentX(JButton.CENTER_ALIGNMENT);
		
    	panelBody.setLayout(layoutBody);
        panelBody.setBorder(borderCommon);
        panelBody.add(titleLabel, BorderLayout.NORTH);
        panelBody.add(selectButton);
        panelBody.add(fileLabel);
        
    	return panelBody;
    }
    // Helper method to create a screen panel 
    private JPanel createScreen(String title) {
        JPanel panel = new JPanel();
        
        BoxLayout layoutTitle = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layoutTitle);
        
        panel.setBorder(borderCommon);
        
        Color titleTextColor = Color.decode("#333333");
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog",Font.PLAIN,72));
		titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        titleLabel.setForeground(titleTextColor);
        
        destinationButton.addActionListener(this);
		destinationButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }
    
    public MultipartFile createMPF(String filename, Path path) {
    	byte[] content = null;
    	try {
    	    content = Files.readAllBytes(path);
    	} catch (final IOException e) {
    	}
    	MultipartFile result = new MPFGUI(content, filename, "application/pdf");
    	return result;
    }
    
    public static void main(String[] args) {
    	new GUI();
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == selectButton) {	    	
	    	//----------------FILE NAME/PATH STUFF---------------------
	        int returnVal = fc.showOpenDialog(selectButton);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String filePath = file.getAbsolutePath();
	            
	            selectButton.setText("Select another");
	            
	            if(filePath.toLowerCase().endsWith(".pdf")) {
	            	fileLabel.setText("Selected file: " + file.getName() + ".");
	            	
	    	        //----------------NEXT BUTTON STUFF----------------------
		            if(!nextListenerAdded) {
		            	switchToScreen2.addActionListener(this);
		            	nextListenerAdded = true;
		            }
		            //-------------------------------------------------------
		            
		            //----------------DESTINATION BUTTON STUFF------------------
		            if (!screen2.isAncestorOf(destinationButton)) {
	                    screen2.add(destinationButton);
	                }
		            //-------------------------------------------------------
	            } else {
	            	fileLabel.setText("Please select a .PDF file.");
	            }        
	            //Resizes window to fit label/new buttons if needed
	            frame.pack();
	        } else {
	        	switchToScreen2.removeActionListener(this);
            	nextListenerAdded = false;
	        	fileLabel.setText("File selection cancelled.");
	        }
	   } 
	   if(e.getSource() == destinationButton) {
			fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int returnVal = fc2.showOpenDialog(null);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
			    File selectedDirectory = fc2.getSelectedFile();
			    String directoryPath = selectedDirectory.getAbsolutePath();
			    SaveDestinationGUI.setDestination(directoryPath);
			    downloadLabel.setText("Selected directory: " + directoryPath);
			    downloadLabel.setAlignmentX(JButton.CENTER_ALIGNMENT);
				screen2.add(downloadLabel);
				//-------------------GENERATE BUTTON-----------------
				if(!generateListenerAdded) {
		           	generateButton.addActionListener(this);
		           	generateListenerAdded = true;
		        }
				if (!screen2.isAncestorOf(generateButton)) {
					screen2.add(Box.createRigidArea(new Dimension(0, 5)));
			    	generateButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
                    screen2.add(generateButton);
                }
				//---------------------------------------------------
			} else {
				generateButton.removeActionListener(this);
				generateListenerAdded = false;
			    downloadLabel.setText("Directory selection cancelled.");
			}
			frame.pack();
	   }
	   if(e.getSource() == generateButton) {
		   File file = fc.getSelectedFile();
           String filePath = file.getAbsolutePath();
           Path path = Paths.get(filePath);
           MultipartFile result = createMPF(filePath, path);
           String design = "design7.pptx";
           
           	try {
        	   PdfToPowerPointConverter.convert(result, design);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   	
           generateSuccess.setAlignmentX(JButton.CENTER_ALIGNMENT);
           screen2.add(generateSuccess);
		   frame.pack();
	   }
    	if (e.getSource() == switchToScreen1) {
    		cardLayout.show(cardPanel, "SCREEN1");
    	}
    	if (e.getSource() == switchToScreen2) {
    		cardLayout.show(cardPanel, "SCREEN2");
    	}
    }
	    	
}
