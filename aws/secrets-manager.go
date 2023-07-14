package aws

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
)

type SecretsManagerClient struct {
	Client *secretsmanager.Client
}

func New() SecretsManagerClient {
	cfg, _ := config.LoadDefaultConfig(context.TODO())
	secretsManagerClient := secretsmanager.NewFromConfig(cfg)
	return SecretsManagerClient{
		Client: secretsManagerClient,
	}
}

func (c *SecretsManagerClient) GetSecret(secretName string) (*secretsmanager.GetSecretValueOutput, error) {
	return c.Client.GetSecretValue(context.TODO(), &secretsmanager.GetSecretValueInput{
		SecretId: &secretName,
	})
}
