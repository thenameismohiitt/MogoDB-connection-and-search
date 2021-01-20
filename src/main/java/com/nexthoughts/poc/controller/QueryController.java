package com.nexthoughts.poc.controller;

import com.nexthoughts.poc.payload.SearchRequest;
import com.nexthoughts.poc.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/query")
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private QueryService queryService;

    @PostMapping("/search")
    public ResponseEntity<?> connectAndSearch(@RequestBody SearchRequest searchRequest) {
        if (searchRequest != null) {
            logger.info("***Connecting to the database***");
            return queryService.createConnectionAndSearch(searchRequest);
        }
        return new ResponseEntity<>("Check the request again!", HttpStatus.NOT_FOUND);
    }
}
