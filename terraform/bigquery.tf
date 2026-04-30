# Couche 1 : RAW (Données brutes)
resource "google_bigquery_dataset" "raw" {
  dataset_id = "sephora_raw_${var.environment}"
  location   = var.region
}

# Couche 2 : DWH (Données nettoyées et mergées)
resource "google_bigquery_dataset" "dwh" {
  dataset_id = "sephora_dwh_${var.environment}"
  location   = var.region
}

# Couche 3 : DM (Données prêtes pour le reporting/Analytics)
resource "google_bigquery_dataset" "dm" {
  dataset_id = "sephora_dm_${var.environment}"
  location   = var.region
}