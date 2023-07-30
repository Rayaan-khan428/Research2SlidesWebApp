package com.example.research2slidesweb;

import org.apache.pdfbox.io.IOUtils;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class BPowerPointGenerator {

    /**
     * @param parentDirectory  - root path of project
     * @param presentation - arraylist containing all the slides
     * @throws IOException
     */
    public static byte[] create(String parentDirectory, ArrayList<Slide> presentation, String design) throws IOException {

        // Get the template folder path (project directory)
        String templatePath = parentDirectory + "/../ppt-templates/" +  design;

        // Create obj that creates a new slideshow with the given template
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(templatePath));

        // Deletes any existing slides in template
        for (int i = ppt.getSlides().size() - 1; i >= 0; i--) {
            ppt.removeSlide(i);
        }

        // load slide master info at index 0,
        XSLFSlideMaster slideOptions = ppt.getSlideMasters().get(0);

        // from the layout options we wish to use TITLE layout for the first slide
        XSLFSlideLayout titleForm = slideOptions.getLayout(SlideLayout.TITLE);

        // now we have loaded the layout, we create a new slide with said layout
        XSLFSlide slide1 = ppt.createSlide(titleForm);

        // add a title to the title slide from the first slide object in presentation
        // array list
        XSLFTextShape title1 = slide1.getPlaceholder(0);
        title1.setText(presentation.get(0).getTitle());

        // set subtitle to empty
        XSLFTextShape subtitle1 = slide1.getPlaceholder(1);
        subtitle1.setText("");

        // for every slide in our presentation arraylist we create a slide
        for (int i = 1; i < presentation.size(); i++) {

            // get title and body format
            XSLFSlideLayout titleAndBody = slideOptions.getLayout(SlideLayout.TITLE_AND_CONTENT);

            // create new slide called midSlide with titleAndBody format
            XSLFSlide midSlide = ppt.createSlide(titleAndBody);

            // set title and body of slide to text from that slide object/section.txt file
            XSLFTextShape slideTitle = midSlide.getPlaceholder(0);
            slideTitle.setText(presentation.get(i).getTitle());
            XSLFTextShape slideBody = midSlide.getPlaceholder(1);

            slideBody.clearText();

            // get body text
            String bodyText = presentation.get(i).getParagraph();

            slideBody.addNewTextParagraph().addNewTextRun().setText(bodyText);

            if (presentation.get(i).getImage().size() != 0) {
                for (int j = 0; j < presentation.get(i).getImage().size(); j++) {
                    XSLFSlide imgSlide = ppt.createSlide();
                    int imageNum = presentation.get(i).getImage().get(j) + 1;
                    File image = new File(parentDirectory + "/image_" + imageNum + ".png");
                    byte[] picture = IOUtils.toByteArray(new FileInputStream(image));
                    XSLFPictureData idx = ppt.addPicture(picture, XSLFPictureData.PictureType.PNG);
                    XSLFPictureShape pic = imgSlide.createPicture(idx);

                    // Calculate the center position of the slide
                    Dimension slideBounds = imgSlide.getSlideShow().getPageSize();
                    int centerX = slideBounds.width / 2;
                    int centerY = slideBounds.height / 2;

                    // Calculate the center position of the image
                    int imageWidth = pic.getPictureData().getImageDimension().width;
                    int imageHeight = pic.getPictureData().getImageDimension().height;
                    int imageX = centerX - (imageWidth / 2);
                    int imageY = centerY - (imageHeight / 2);

                    // Set the position of the image at the center
                    pic.setAnchor(new Rectangle(imageX, imageY, imageWidth, imageHeight));
                }
            }
        }

        // Save the PowerPoint presentation to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ppt.write(byteArrayOutputStream);

        // creating an FileOutputStream object
        File file = new File(presentation.get(0).getTitle() + ".pptx");
        FileOutputStream out = new FileOutputStream(parentDirectory + "/" + file);

        //----------------------NEW GUI CHANGES----------------------
        String directoryGUI = SaveDestinationGUI.getDestination();
        if(directoryGUI != null) {
            out = new FileOutputStream(directoryGUI + "/" + file);
        }
        //---

        // saving the changes to a file
        ppt.write(out);
        System.out.println("Presentation created successfully");
        out.close();
        ppt.close();

        return byteArrayOutputStream.toByteArray();

    }

}
