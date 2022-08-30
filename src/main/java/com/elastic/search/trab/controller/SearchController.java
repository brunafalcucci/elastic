package com.elastic.search.trab.controller;

import com.elastic.search.trab.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elastic.search.trab.model.ELManager;

import java.util.List;


@RestController
@RequestMapping("/")
public class SearchController {
    @GetMapping(value = "/{must}/{not}/{should}/{page}")
    public List<Result> getResult(@PathVariable String must, @PathVariable String not, @PathVariable String should, @PathVariable Integer page){
        String esHost = "localhost";
        int esPort = 9200;
        String esIndex = "wikipedia";

        ELManager esm = new ELManager(esHost, esPort, esIndex);

        return esm.search(must, not, should, page, 10);

    }
}
