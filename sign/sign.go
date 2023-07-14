package sign

import (
	"github.com/gin-gonic/gin"
	"github.com/hunoz/bifrost-api/handling"
	v1 "github.com/hunoz/maroon-api/api/v1"
	"github.com/sirupsen/logrus"
)

type SignController struct {
	Signer CloudFrontSigner
}

func NewSignController(cloudfrontKeypairId string, privateKeySecretId string) SignController {
	return SignController{
		Signer: NewCloudFrontSigner(cloudfrontKeypairId, privateKeySecretId),
	}
}

func (c *SignController) Sign(ctx *gin.Context) {
	var input handling.SignInput
	username, _ := ctx.Get("username")

	if err := ctx.ShouldBindQuery(&input); err != nil {
		ctx.JSON(400, v1.BadRequestError())
		return
	}

	logrus.Infof("User '%s' requesting signed cookies for uri: %s", username, input.UriToSign)

	output, err := c.Signer.GenerateSignedCookies(input.UriToSign, input.SecondsToExpiration)

	if err != nil {
		logrus.Errorf("Error generating signed cookies: %s", err.Error())
		ctx.JSON(500, v1.InternalServerError())
		return
	}

	logrus.Infof("Successfully generated signed cookies for uri: %s", input.UriToSign)

	ctx.JSON(200, v1.JSONResponse[map[string]string]{Data: output})
}
