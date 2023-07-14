package handling

type SignInput struct {
	UriToSign           string `json:"uriToSign" binding:"required" form:"uriToSign"`
	SecondsToExpiration int    `json:"secondsToExpiration" binding:"required,numeric" form:"secondsToExpiration"`
}
