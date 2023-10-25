package com.example.research2slidesweb;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://research2-slides-front-pb1jbmyl5-rayaan-khan428.vercel.app/"})
public class Controller {

    private ConcurrentMap<String, CompletableFuture<byte[]>> pendingTasks = new ConcurrentHashMap<>();

    @PostMapping("/convert")
    public ResponseEntity<String> convertPdfToPowerPoint(
            @RequestParam("design") String design,
            @RequestBody MultipartFile pdfFile) {

        final String finalDesign = design + ".pptx";  // Create a final variable here

        String taskId = UUID.randomUUID().toString();

        CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("request received successfully");
                return PdfToPowerPointConverter.convert(pdfFile, finalDesign);  // Use the final variable here
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        pendingTasks.put(taskId, future);

        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/convert/status/{taskId}")
    public ResponseEntity<String> checkStatus(@PathVariable String taskId) {
        if (!pendingTasks.containsKey(taskId)) {
            return ResponseEntity.notFound().build();
        }

        CompletableFuture<byte[]> future = pendingTasks.get(taskId);
        if (future.isDone()) {
            return ResponseEntity.ok("Done");
        } else {
            return ResponseEntity.ok("Processing");
        }
    }

    @GetMapping("/convert/result/{taskId}")
    public ResponseEntity<byte[]> fetchResult(@PathVariable String taskId) {
        if (!pendingTasks.containsKey(taskId)) {
            return ResponseEntity.notFound().build();
        }

        CompletableFuture<byte[]> future = pendingTasks.get(taskId);
        if (!future.isDone()) {
            return ResponseEntity.status(202).body("Still processing".getBytes());
        }

        try {
            byte[] convertedPresentation = future.get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "converted_presentation.pptx");

            return ResponseEntity.ok().headers(headers).body(convertedPresentation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Error occurred during conversion: " + e.getMessage().getBytes()).getBytes());
        }
    }
}
