package com.supplify.controller;

import java.io.File;
import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.supplify.services.FileProcessingService;

@Controller
//@RequestMapping("/file")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @GetMapping("/list")
    public ResponseEntity<?> getFileList() {
        return new ResponseEntity<>(fileProcessingService.fileList(), HttpStatus.OK);
    }

	/*
	 * @GetMapping(value = "/download/{name}", produces =
	 * MediaType.APPLICATION_OCTET_STREAM_VALUE) public ResponseEntity<?>
	 * downloadFile(@PathVariable(value = "name") String fileName) { Resource file =
	 * fileProcessingService.downloadFile(fileName); if (file == null) { return new
	 * ResponseEntity<>(HttpStatus.NOT_FOUND); } else { return
	 * ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file
	 * ); } }
	 */

    @GetMapping(value = "/download/{name}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadFile(@PathVariable(value = "name") String fileName) {
       // Resource filee = fileProcessingService.downloadFile("sellarDoc.pdf");
    	System.out.println("FileName :"+fileName);
    	try {
    	String basePath="C:/supplifyDocs/documentuploads/";
       
    	File file = new File(basePath + "sellarDoc.pdf");
        
    	HttpHeaders headers = new HttpHeaders();      
        headers.add("content-disposition", "inline;filename=" +file.getName());
        
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
        	 return ResponseEntity.ok()
                     .headers(headers)
                     .contentLength(file.length())
                     .contentType(MediaType.parseMediaType("application/pdf"))
                     .body(resource);
        	//return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
        }
    	}catch (Exception e) {
    		  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	       	// TODO: handle exception
		}
    }
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) {
        String status = fileProcessingService.uploadFile(file);
        return "CREATED".equals(status) ? new ResponseEntity<>(HttpStatus.CREATED) : ("EXIST".equals(status) ? new ResponseEntity<>(HttpStatus.NOT_MODIFIED) : new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED));
    }

}