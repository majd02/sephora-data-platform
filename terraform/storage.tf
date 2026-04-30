resource "google_storage_bucket" "raw_data" {
  # Note : Le nom doit être unique mondialement sur GCP
  name          = "sephora-raw-data-${var.project_id}-${var.environment}"
  location      = var.region
  force_destroy = true

  uniform_bucket_level_access = true
}