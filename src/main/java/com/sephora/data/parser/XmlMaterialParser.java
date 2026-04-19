package com.sephora.data.parser;

import com.sephora.data.model.Material;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlMaterialParser implements SephoraFileParser<Material> {

    @Override
    public List<Material> parse(String filePath) throws Exception {
        List<Material> materials = new ArrayList<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(filePath));

        while (reader.hasNext()) {
            int event = reader.next();

            // On cherche le début d'un élément "material"
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("material".equals(reader.getLocalName())) {

                    // On initialise des variables temporaires pour stocker les données du bloc actuel
                    int id = 0;
                    int productId = 0;
                    String matiere = "";
                    String fournisseur = "";
                    String certification = "";

                    // On boucle tant qu'on n'a pas trouvé la fin de </material>
                    while (reader.hasNext()) {
                        int innerEvent = reader.next();

                        if (innerEvent == XMLStreamConstants.START_ELEMENT) {
                            String tag = reader.getLocalName();

                            // On lit le texte à l'intérieur de chaque balise
                            if ("id".equals(tag)) id = Integer.parseInt(reader.getElementText());
                            else if ("product_id".equals(tag)) productId = Integer.parseInt(reader.getElementText());
                            else if ("matiere".equals(tag)) matiere = reader.getElementText();
                            else if ("fournisseur".equals(tag)) fournisseur = reader.getElementText();
                            else if ("certification".equals(tag)) certification = reader.getElementText();
                        }

                        // Si on arrive à la balise fermante </material>, on sort de la boucle interne
                        else if (innerEvent == XMLStreamConstants.END_ELEMENT) {
                            if ("material".equals(reader.getLocalName())) {
                                materials.add(new Material(id, productId, matiere, fournisseur, certification));
                                break;
                            }
                        }
                    }
                }
            }
        }
        reader.close();
        return materials;
    }
}