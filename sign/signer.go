package sign

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"strings"
	"time"

	aws "github.com/hunoz/bifrost-api/aws"
)

var sanitizedMap = map[string]string{"+": "-", "=": "_", "/": "~"}

type CloudFrontSigner struct {
	SecretsManagerClient *aws.SecretsManagerClient
	CloudFrontKeypairId  string
	PrivateKeySecretId   string
}

func NewCloudFrontSigner(cloudfrontKeypairId string, privateKeySecretId string) CloudFrontSigner {
	secretsManagerClient := aws.New()
	return CloudFrontSigner{
		SecretsManagerClient: &secretsManagerClient,
		CloudFrontKeypairId:  cloudfrontKeypairId,
		PrivateKeySecretId:   privateKeySecretId,
	}
}

func (c *CloudFrontSigner) makePolicy(resourceUri string, expirationDate time.Time) string {
	policy := make(map[string][]any)
	policy["Statement"] = append(policy["Statement"], map[string]any{
		"Resource": resourceUri,
		"Condition": map[string]any{
			"DateLessThan": map[string]int{
				"AWS:EpochTime": int(expirationDate.Unix()),
			},
		},
	})

	policyJson, _ := json.Marshal(policy)
	return string(policyJson)
}

func (c *CloudFrontSigner) transformPrivateKey(privateKey []byte) (*rsa.PrivateKey, error) {
	block, _ := pem.Decode(privateKey)
	der, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		return nil, fmt.Errorf("error parsing private key: %s", err.Error())
	}
	return der, nil
}

func (c *CloudFrontSigner) sign(policy string) ([]byte, error) {
	response, err := c.SecretsManagerClient.GetSecret(c.PrivateKeySecretId)
	if err != nil {
		return nil, fmt.Errorf("error getting private key: %s", err.Error())
	}

	policyToSign := sha256.Sum256([]byte(policy))

	privateKey, err := c.transformPrivateKey([]byte(*response.SecretString))
	if err != nil {
		return nil, err
	}

	signature, err := rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA256, policyToSign[:])
	if err != nil {
		return nil, fmt.Errorf("error signing private key: %s", err.Error())
	}

	return signature, nil
}

func (c *CloudFrontSigner) sanitizeBase64(stringToSanitize string) string {
	for key, value := range sanitizedMap {
		stringToSanitize = strings.Replace(stringToSanitize, key, value, -1)
	}
	return stringToSanitize
}

func (c *CloudFrontSigner) GenerateSignedCookies(uriToSign string, secondsBeforeExpiration int) (map[string]string, error) {
	expiresOn := time.Now().Add(time.Second * time.Duration(secondsBeforeExpiration))
	policy := c.makePolicy(uriToSign, expiresOn)

	signature, err := c.sign(policy)
	if err != nil {
		return nil, err
	}

	encodedPolicy := base64.StdEncoding.EncodeToString([]byte(policy))
	encodedSignature := base64.StdEncoding.EncodeToString([]byte(signature))

	return map[string]string{
		"CloudFront-Policy":      c.sanitizeBase64(encodedPolicy),
		"CloudFront-Signature":   c.sanitizeBase64(encodedSignature),
		"CloudFront-Key-Pair-Id": c.CloudFrontKeypairId,
	}, nil
}
