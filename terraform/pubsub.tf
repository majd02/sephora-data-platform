# On appelle le module pour le premier topic
module "ingestion_notifications" {
  source      = "./modules/pubsub"
  topic_name  = "sephora-ingestion-notifications"
  environment = var.environment
}

# On appelle le même module pour le deuxième topic
module "processing_logs" {
  source      = "./modules/pubsub"
  topic_name  = "sephora-processing-logs"
  environment = var.environment
}