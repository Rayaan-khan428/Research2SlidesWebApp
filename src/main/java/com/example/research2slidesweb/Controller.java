package com.example.research2slidesweb;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController // indicates the class is a Spring MVC controller that handles HTTP requests/responses
@RequestMapping("/api") // all requests with this in the url will map to this controller
@CrossOrigin(origins = "http://localhost:3000")
public class Controller {

    /**
     * this method takes the information sent from the front end, runs it through the methods that will convert the pdf
     * to a PowerPoint and returns a PowerPoint that is ready for download
     *
     * @PostMapping - any api requests made will be run through this method
     * @param design - a string indicating which design
     * @param pdfFile - the pdf that will be converted
     * @return - a byte array which is the PowerPoint
     */
    @CrossOrigin(origins = "https://research2-slides-front-pb1jbmyl5-rayaan-khan428.vercel.app/")
    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertPdfToPowerPoint(

            @RequestParam("design") String design,
            @RequestBody MultipartFile pdfFile)
    {
        try {

            System.out.println("request received successfully");

            design += ".pptx";

            // Call the PdfToPowerPointConverter class to perform the conversion
            byte[] convertedPresentation = PdfToPowerPointConverter.convert(pdfFile, design);

            // Setting the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "converted_presentation.pptx");

            // Return the converted presentation as a ResponseEntity
            return ResponseEntity.ok().headers(headers).body(convertedPresentation);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            // Return an error response if conversion fails
            return ResponseEntity.status(500).body(("Error occurred during conversion: " + e.getMessage().getBytes()).getBytes());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
