import * as aws from "@pulumi/aws";

export function createCloudFrontDistribution(
    contentBucket: aws.s3.Bucket,
    imagesBucket: aws.s3.Bucket,
    staticResourcesBucket: aws.s3.Bucket,
    originAccessIdentity: aws.cloudfront.OriginAccessIdentity
) {
    const cloudfrontDistribution = new aws.cloudfront.Distribution("websiteDistribution", {
        origins: [
            {
                originId: contentBucket.id,
                domainName: contentBucket.websiteEndpoint,
                customOriginConfig: {
                    httpPort: 80,
                    httpsPort: 443,
                    originSslProtocols: ["TLSv1.2"],
                    originProtocolPolicy: "https-only",
                },
            },
            {
                originId: imagesBucket.id,
                domainName: imagesBucket.bucketRegionalDomainName,
                s3OriginConfig: {
                    originAccessIdentity: originAccessIdentity.cloudfrontAccessIdentityPath,
                },
            },
            {
                originId: staticResourcesBucket.id,
                domainName: staticResourcesBucket.bucketRegionalDomainName,
                s3OriginConfig: {
                    originAccessIdentity: originAccessIdentity.cloudfrontAccessIdentityPath,
                },
            },
        ],
        restrictions: {
            geoRestriction: {
                restrictionType: "none",
            },
        },
        defaultRootObject: "home",
        enabled: true,
        defaultCacheBehavior: {
            targetOriginId: contentBucket.id,
            viewerProtocolPolicy: "redirect-to-https",
            allowedMethods: ["GET", "HEAD"],
            cachedMethods: ["GET", "HEAD"],
            forwardedValues: {
                cookies: {
                    forward: "none",
                },
                queryString: false,
            },
            minTtl: 0,
            maxTtl: 86400,
            defaultTtl: 3600,
        },
        priceClass: "PriceClass_100",
        viewerCertificate: {
            cloudfrontDefaultCertificate: true,
        },
    });

    return cloudfrontDistribution;
}
