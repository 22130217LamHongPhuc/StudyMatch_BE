# Configuration variables
$PROJECT_ID = "studymatch-502204"
$REGION = "asia-southeast1"
$CONNECTION_NAME = "studymatch-github"
$REPO_NAME = "22130217LamHongPhuc-StudyMatch_BE"

# We use the default Compute Engine service account because it has the necessary permissions to build, push to Artifact Registry, and deploy to Cloud Run
$SERVICE_ACCOUNT = "projects/$PROJECT_ID/serviceAccounts/36417266010-compute@developer.gserviceaccount.com"

$REPO_PATH = "projects/$PROJECT_ID/locations/$REGION/connections/$CONNECTION_NAME/repositories/$REPO_NAME"

# Service configuration
$SERVICE_NAME = "hate-speech-service"
$CONFIG_PATH = "hate_speech_service/cloudbuild.yaml"
$FILTER_PATTERN = "hate_speech_service/**"

$TRIGGER_NAME = "deploy-$SERVICE_NAME"

Write-Host "Creating trigger with the correct Build Service Account for: $SERVICE_NAME on project: $PROJECT_ID..." -ForegroundColor Cyan
Write-Host "--------------------------------------------------"

# Check if trigger already exists and delete it first
$existing = gcloud builds triggers list --region=$REGION --project=$PROJECT_ID --filter="name=$TRIGGER_NAME" --format="value(name)" 2>$null
if ($existing) {
    Write-Host "Trigger '$TRIGGER_NAME' already exists. Deleting it..." -ForegroundColor Yellow
    gcloud builds triggers delete $TRIGGER_NAME --region=$REGION --project=$PROJECT_ID --quiet
}

Write-Host "Creating trigger: $TRIGGER_NAME"
Write-Host "Config file: $CONFIG_PATH"
Write-Host "Filter: $FILTER_PATTERN"

gcloud builds triggers create github `
    --name=$TRIGGER_NAME `
    --region=$REGION `
    --repository=$REPO_PATH `
    --branch-pattern="^main$" `
    --build-config=$CONFIG_PATH `
    --included-files=$FILTER_PATTERN `
    --project=$PROJECT_ID `
    --service-account=$SERVICE_ACCOUNT

Write-Host "--------------------------------------------------"
Write-Host "Trigger '$TRIGGER_NAME' created successfully!" -ForegroundColor Green
