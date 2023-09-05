### Description
The Bifrost API serves as the bridge between the two realms: Applications needing proper authentication mechansims, and the public internet. This package accomplishes that by providing two functionalities: sign URLs for CloudFront distributions and endpoints for fetching the user information. This is accomplished by all non-error related endpoints requiring authentication via OIDC. The user information is stored in a 20 hour session thus making it possible to protect CloudFront distributions as well as fetch user information from a frontend library (coming soon) and obtain the ID token for JWT-protected API gateways.

### Usage / Installation
1. Pull the image: `docker pull ghcr.io/PopAxe/BifrostAPI` or `docker pull hunoz/bifrost-api`
2. Set up the required environment variables for development or deployment, whichever mode you require

### Development
To run this application locally, you must navigate the the Vault website, login, and copy your token. Then, run the following commands to run the application locally:
```
VAULT_HOSTNAME=<VAULT_HOSTNAME> \
VAULT_PORT=<VAULT_PORT> \
VAULT_TOKEN=<VAULT_TOKEN> gradle run
```

For explanation of the variables, please see the section '[Environment Variables For Development](#Environment-Variables-For-Development)'.

#### Notes
If a new Cloudfront keypair needs to be generated, you need to do the following:
1. Generate the keypair in AWS console
2. Download the private key
3. Copy the contents of the file into the corresponding secret in Vault.
4. Place the full file contents in the secret where the private key is stored
5. Restart the service to pick up the new private key

#### Environment Variables For Deployment
* VAULT_HOSTNAME - The hostname of the vault instance / cluster housing the settings for Bifrost API to run
* VAULT_PORT - The port of the vault instance / cluster housing the settings for Bifrost API to run
* VAULT_ROLEID - The role ID to interact with Vault
* VAULT_SECRETID - The secret ID to interact with vault

#### Environment Variables For Development
* VAULT_HOSTNAME - The hostname of the vault instance / cluster housing the settings for Bifrost API to run. For example, if the endpoint URL is https://vault.com, then the environment variable would be `vault.com`.
* VAULT_PORT - The port of the vault instance / cluster housing the settings for Bifrost API to run
* VAULT_TOKEN - Your token for interacting with Vault