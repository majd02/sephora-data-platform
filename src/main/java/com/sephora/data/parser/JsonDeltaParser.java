package com.sephora.data.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sephora.data.model.DeltaOperation;
import com.sephora.data.model.Store;
import java.io.File;
import java.util.List;

public class JsonDeltaParser {
    public List<DeltaOperation<Store>> parseDelta(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // On indique à Jackson de lire une liste de DeltaOperation
        return mapper.readValue(new File(filePath), new TypeReference<List<DeltaOperation<Store>>>() {});
    }
}