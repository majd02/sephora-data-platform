package com.sephora.data.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sephora.data.model.Product;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonProductParser {

    public List<Product> parse(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                new File(filePath),
                new TypeReference<List<Product>>() {}
        );
    }
}