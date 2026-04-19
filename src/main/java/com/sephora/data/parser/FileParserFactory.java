package com.sephora.data.parser;

public class FileParserFactory {
    public static SephoraFileParser<?> getParser(String filePath) {
        if (filePath.endsWith(".csv"))
            if (filePath.contains("store"))
                return new CsvStoreParser();
            if (filePath.contains("brand"))
                return  new CsvBrandParser();
        if (filePath.endsWith(".json"))
            return new JsonProductParser();
        if (filePath.endsWith(".xml"))
            return new XmlMaterialParser();

        throw new IllegalArgumentException("Format non supporté : " + filePath);
    }
}