resource "google_pubsub_topic" "topic" {
  name = "${var.topic_name}-${var.environment}"
}

# CORRECTION ICI : "google_pubsub_subscription" au lieu de "google_pubsub_topic_subscription"
resource "google_pubsub_subscription" "subscription" {
  name  = "${var.topic_name}-sub-${var.environment}"
  topic = google_pubsub_topic.topic.id
}