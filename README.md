# Push code to Github

## Authenticate

    % gh auth login

## Add a new module to Google Run

### Maven module 
1. Create a new dir <code>/[NEW_MODULE]</code>

### Google Run

    databiit % cd [NEW_MODULE]
    databiit % cp ./service-app/cloudbuild.yaml ./[NEW_MODULE] 
    databiit % cp ./service-app/Dockerfile ./[NEW_MODULE]

Replace 'service-app' with '[NEW_MODULE]'


