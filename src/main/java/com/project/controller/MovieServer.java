package com.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.codec.ResourceRegionEncoder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/movie")
public class MovieServer {


    @GetMapping({"","/"})
    public String getMovie(@RequestParam(value = "name" , required = false , defaultValue = "") String movieName, Model model ) throws IOException {

        File files = new File("F:/Webseries/");
        if(movieName.isEmpty()){
            model.addAttribute("emp",true);
            return "page";
        }
        Optional<File> fileOptional = Arrays.stream(files.listFiles()).filter(file -> file.getName().toLowerCase().contains(movieName.toLowerCase())).findFirst();
        if(fileOptional.isEmpty()){model.addAttribute("notFound" , true) ;return "page";}

//        model.addAttribute("moviePath",  fileOptional.get().toURI().toString().replaceFirst("/","///"));
        String vid = "/repo/" + fileOptional.get().getName();
        model.addAttribute("moviePath", vid);
        return "page";
    }


    @GetMapping("/repo/**")
    @ResponseBody
    public ResponseEntity<ResourceRegion> getRangeStream(
            HttpServletRequest request , @RequestHeader(name="Range" , required = false) String rangeHeader
    ){
        String filePath = request.getRequestURI().substring("/movies".length());
        Path vidPath = Paths.get("F:/Webseries", filePath);

        Resource vidResource = new FileSystemResource(vidPath);

        try{
            ResourceRegion region = resourceRegion(vidResource , rangeHeader);
            return
                    ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).
                            contentType(MediaTypeFactory.getMediaType(vidResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                            .header(ACCEPT_RANGES , BYTES)
                            .header(CONTENT_RANGE , "bytes " + region.getPosition() + "-" + (region.getPosition() + region.getCount()-1) + "/" + vidResource.contentLength())
                            .body(region);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_RANGE = "Content-Range";
    private static final String ACCEPT_RANGES = "Accept-Ranges";
    private static final String BYTES = "bytes";
    private static final int BYTE_RANGE = 4096;
    private ResourceRegion resourceRegion(Resource vidResource, String rangeHeader) throws IOException {
        long contentLength = vidResource.contentLength() ;
        if(rangeHeader.isEmpty()){
            long rangeLength = Math.min(BYTE_RANGE, contentLength);
            return new ResourceRegion(vidResource, 0, rangeLength);
        }
        String[] ranges =  rangeHeader.substring("bytes=".length()).split("-");
        System.out.println(Arrays.toString(ranges));
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : contentLength - 1;
        long rangeLength = Math.min(BYTE_RANGE  , end-start + 1);

        return new ResourceRegion(vidResource ,start , rangeLength);
    }


}
