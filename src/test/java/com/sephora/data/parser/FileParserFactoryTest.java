package com.sephora.data.parser;

import com.sephora.data.model.*;

public class FileParserFactoryTest {

    public static void main(String[] args) {
        System.out.println("=== DEBUT DU TEST DE LA FACTORY ===");

        try {
            // Test 1 : CSV Store
            checkParser("stores.csv", CsvStoreParser.class);

            // Test 2 : JSON Product
            checkParser("products.json", JsonProductParser.class);

            // Test 3 : XML Material
            checkParser("materials.xml", XmlMaterialParser.class);

            // Test 4 : Format inconnu (doit lever une exception)
            try {
                FileParserFactory.getParser("image.png");
                System.out.println("Erreur : La factory aurait dû refuser le .png");
            } catch (IllegalArgumentException e) {
                System.out.println("Succès : Format inconnu bien détecté");
            }

        } catch (Exception e) {
            System.err.println("Crash pendant le test : " + e.getMessage());
        }
    }

    private static void checkParser(String fileName, Class<?> expectedClass) {
        SephoraFileParser<?> parser = FileParserFactory.getParser(fileName);

        if (expectedClass.isInstance(parser)) {
            System.out.println("Succès : " + fileName + " -> " + expectedClass.getSimpleName());
        } else {
            System.out.println(fileName + " a retourné " + parser.getClass().getSimpleName());
        }
    }
}