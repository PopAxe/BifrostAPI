### Description

### Notes
If a new Cloudfront keypair needs to be generated, you need to do the following:
1. Generate the keypair in AWS console
2. Download the private key
3. Run `openssl pkcs8 -topk8 -inform PEM -outform PEM -in <PK_FILE> -out privatekey.pem -nocrypt` replacing PK_FILE with the path to the private key file
4. Place the full file contents in the secret where the private key is stored
5. Restart the service to pick up the new private key (if needed)

### Environment Variables
CLIENT_ID - The ID of the OIDC client that was created for this application
CLIENT_SECRET - The secret of the OIDC client that was created for this application
KEYPAIR_ID - The keypair ID of the Cloudfront keypair
MONGO_USERNAME - The username for the MongoDB database session store
MONGO_PASSWORD - The password for the MongoDB database session store
MONGO_HOST - The hostname for the MongoDB database session store
MONGO_DB - The database name for the MongoDB database session store
ENTRYPOINT - Either 'BifrostApiApplication' if running locally or 'BifrostApiLambdaApplication' if running in lambda