package com.sephora.data.pipeline;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.EmptyMatchTreatment;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class ProductIngestionPipeline {

    // --- CLASSES DO-FN (LES PARSEURS) ---

    public static class CsvStoreDoFn extends DoFn<String, TableRow> {
        @ProcessElement
        public void processElement(@Element String line, OutputReceiver<TableRow> out) {
            if (line.startsWith("id")) return; // Sauter le header
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                out.output(new TableRow()
                        .set("id", parts[0].trim())
                        .set("nom", parts[1].trim())
                        .set("ville", parts[2].trim()));
            }
        }
    }

    public static class JsonProductDoFn extends DoFn<String, TableRow> {
        // On passe la PCollectionView pour l'enrichissement
        private final PCollectionView<Map<String, String>> brandView;
        public JsonProductDoFn(PCollectionView<Map<String, String>> brandView) {
            this.brandView = brandView;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            String json = c.element();
            Map<String, String> brands = c.sideInput(brandView);
            try {
                JSONObject obj = new JSONObject(json);
                String brandId = obj.optString("brand_id", "0");
                c.output(new TableRow()
                        .set("id", obj.optString("id"))
                        .set("nom", obj.optString("nom"))
                        .set("prix", obj.optDouble("prix", 0.0))
                        .set("brand_id", brandId)
                        .set("brand_name", brands.getOrDefault(brandId, "Inconnu")));
            } catch (Exception e) {
                System.err.println("Erreur format JSON : " + json);
            }
        }
    }

    public static class XmlMaterialDoFn extends DoFn<String, TableRow> {
        @ProcessElement
        public void processElement(@Element String xml, OutputReceiver<TableRow> out) {
            // On vérifie que la ligne contient TOUTES les balises nécessaires
            if (xml.contains("<id>") && xml.contains("<nom>")) {
                try {
                    String id = xml.split("<id>")[1].split("</id>")[0];
                    String nom = xml.split("<nom>")[1].split("</nom>")[0];
                    out.output(new TableRow().set("id", id).set("nom", nom));
                } catch (Exception e) {
                    // On ignore silencieusement les lignes de structure (ex: <materials>)
                }
            }
        }
    }
    // --- MAIN PIPELINE ---

    public static void main(String[] args) {
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().create();
        Pipeline pipeline = Pipeline.create(options);

        String bucket = "gs://sephora-raw-data-project-e8660359-eb66-4c3c-8c3-dev/";
        String projectId = "project-e8660359-eb66-4c3c-8c3";

        // 1. SCHÉMAS BIGQUERY
        TableSchema storeSchema = new TableSchema().setFields(Arrays.asList(
                new TableFieldSchema().setName("id").setType("STRING"),
                new TableFieldSchema().setName("nom").setType("STRING"),
                new TableFieldSchema().setName("ville").setType("STRING")));

        TableSchema materialSchema = new TableSchema().setFields(Arrays.asList(
                new TableFieldSchema().setName("id").setType("STRING"),
                new TableFieldSchema().setName("nom").setType("STRING")));

        TableSchema productSchema = new TableSchema().setFields(Arrays.asList(
                new TableFieldSchema().setName("id").setType("STRING"),
                new TableFieldSchema().setName("nom").setType("STRING"),
                new TableFieldSchema().setName("prix").setType("FLOAT"),
                new TableFieldSchema().setName("brand_id").setType("STRING"),
                new TableFieldSchema().setName("brand_name").setType("STRING")
        ));

        // 2. SIDE INPUT : BRANDS (pour enrichir les produits)
        PCollectionView<Map<String, String>> brandView = pipeline
                .apply("ReadBrands", TextIO.read().from(bucket + "full/brands/brands_full.csv"))
                .apply("ParseBrands", ParDo.of(new DoFn<String, KV<String, String>>() {
                    @ProcessElement
                    public void processElement(@Element String line, OutputReceiver<KV<String, String>> out) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2 && !parts[0].equals("id")) {
                            out.output(KV.of(parts[0].trim(), parts[1].trim()));
                        }
                    }
                }))
                .apply("ToBrandMap", View.asMap());

        // --- BRANCHE 1 : STORES (CSV) ---
        PCollection<TableRow> storeRows = pipeline
                .apply("ReadStoresCSV", TextIO.read().from(bucket + "full/stores/stores_full.csv"))
                .apply("ParseStoresCSV", ParDo.of(new CsvStoreDoFn()));

        storeRows.apply("WriteStoresBQ", BigQueryIO.writeTableRows()
                .to(projectId + ":sephora_raw.raw_stores_beam")
                .withSchema(storeSchema)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));
        // <--- AJOUT BACKUP STORES ICI
        storeRows.apply("BackupStoresGCS", MapElements.into(TypeDescriptors.strings())
                        .via(row -> row.get("id") + "," + row.get("nom") + "," + row.get("ville")))
                .apply("WriteStoresBackup", TextIO.write().to(bucket + "backups/stores_backup").withSuffix(".csv"));

        // --- BRANCHE 2 : PRODUCTS (JSON + ENRICHISSEMENT) ---
        PCollection<TableRow> productRows = pipeline
                .apply("ReadProductsJSON", TextIO.read().from(bucket + "full/products/products_full.json"))
                .apply("ParseAndEnrich", ParDo.of(new JsonProductDoFn(brandView)).withSideInputs(brandView));

        productRows.apply("WriteProductsBQ", BigQueryIO.writeTableRows()
                .to(projectId + ":sephora_raw.raw_products_beam")
                .withSchema(productSchema)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));
        // <--- AJOUT BACKUP PRODUCTS ICI
        productRows.apply("BackupProductsGCS", MapElements.into(TypeDescriptors.strings())
                        .via(row -> row.get("id") + "," + row.get("nom") + "," + row.get("brand_name")))
                .apply("WriteProductsBackup", TextIO.write().to(bucket + "backups/products_backup").withSuffix(".csv"));

        // --- BRANCHE 3 : MATERIALS (XML) ---
        PCollection<TableRow> materialRows = pipeline
                .apply("ReadMaterialsXML", TextIO.read().from(bucket + "full/materials/materials_full.xml"))
                .apply("ParseMaterialsXML", ParDo.of(new XmlMaterialDoFn()));

        materialRows.apply("WriteMaterialsBQ", BigQueryIO.writeTableRows()
                .to(projectId + ":sephora_raw.raw_materials_beam")
                .withSchema(materialSchema)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));
        // <--- AJOUT BACKUP MATERIALS ICI
        materialRows.apply("BackupMaterialsGCS", MapElements.into(TypeDescriptors.strings())
                        .via(row -> row.get("id") + "," + row.get("nom")))
                .apply("WriteMaterialsBackup", TextIO.write().to(bucket + "backups/materials_backup").withSuffix(".csv"));

// --- BRANCHE 4 : SALES DELTA (JSON) ---
        TableSchema salesSchema = new TableSchema().setFields(Arrays.asList(
                new TableFieldSchema().setName("sale_id").setType("STRING"),
                new TableFieldSchema().setName("product_id").setType("STRING"),
                new TableFieldSchema().setName("store_id").setType("STRING"),
                new TableFieldSchema().setName("quantite").setType("INTEGER"),
                new TableFieldSchema().setName("date_vente").setType("STRING")));

        pipeline.apply("ReadSalesDelta", TextIO.read().from(bucket + "deltas/sales_delta.json"))
                .apply("ParseSales", ParDo.of(new SalesDeltaDoFn()))
                .apply("WriteToStaging", BigQueryIO.writeTableRows()
                        .to(projectId + ":sephora_raw.sales_staging") // <--- Table tampon
                        .withSchema(salesSchema)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE)
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED));
        // Lancement du pipeline
        pipeline.run().waitUntilFinish();


// UNE FOIS FINI : On lance le MERGE SQL
        String mergeQuery = "MERGE sephora_raw.sales_final T " +
                "USING sephora_raw.sales_staging S " +
                "ON T.sale_id = S.sale_id " +
                "WHEN MATCHED THEN UPDATE SET T.quantite = S.quantite, T.date_vente = S.date_vente " +
                "WHEN NOT MATCHED THEN INSERT (sale_id, product_id, store_id, quantite, date_vente) " +
                "VALUES(sale_id, product_id, store_id, quantite, date_vente)";

 
    }
    public static class SalesDeltaDoFn extends DoFn<String, TableRow> {
        @ProcessElement
        public void processElement(@Element String json, OutputReceiver<TableRow> out) {
            // On vérifie que la ligne contient une clé du JSON (ex: "product_id")
            if (json.contains("product_id")) {
                try {
                    JSONObject obj = new JSONObject(json);

                    // On crée la ligne pour BigQuery en utilisant les clés EXACTES du JSON
                    TableRow row = new TableRow()
                            .set("sale_id", String.valueOf(obj.getInt("id")))         // "id" dans le JSON
                            .set("product_id", String.valueOf(obj.getInt("product_id")))
                            .set("store_id", String.valueOf(obj.getInt("store_id")))
                            .set("quantite", obj.getInt("quantite"))
                            .set("date_vente", obj.getString("date"));                // "date" dans le JSON

                    out.output(row);
                } catch (Exception e) {
                    System.err.println("Erreur sur la ligne : " + json.trim());
                }
            }
        }
    }
}