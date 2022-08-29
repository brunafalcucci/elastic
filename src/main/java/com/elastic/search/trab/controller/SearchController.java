package com.elastic.search.trab.controller;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elastic.search.trab.model.ELManager;


@RestController
@RequestMapping("/")
public class SearchController {
    @GetMapping(value = "/{must}/{not}/{should}")
    public String getResult(@PathVariable String must, @PathVariable String not, @PathVariable String should){
        String esHost = "localhost";
        String result;
        int esPort = 9200;
        String esIndex = "wikipedia";
        ELManager esm = new ELManager(esHost, esPort, esIndex);

        return esm.search(must, not, should, 1, 1000).toString();

    }
}
