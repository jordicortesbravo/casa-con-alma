import * as aws from "@pulumi/aws";
import * as pulumi from '@pulumi/pulumi';

export function createRedirectFromRootToWWW(
    certificate: aws.acm.Certificate,
    validationRecords: pulumi.Output<aws.route53.Record[]>
) {
    const redirectBucket = new aws.s3.Bucket("casaconalma.com", {
        bucket: "casaconalma.com",
        website: {
            redirectAllRequestsTo: "https://www.casaconalma.com"
        },
    });
    
    const redirectCloudfront = new aws.cloudfront.Distribution("redirect-root-to-www-cloudfront-distribution", {
        origins: [
            {
                originId: redirectBucket.id,
                domainName: redirectBucket.websiteEndpoint,
                customOriginConfig: {
                    httpPort: 80,
                    httpsPort: 443,
                    originProtocolPolicy: "http-only",
                    originSslProtocols: ["TLSv1.2"],
                },
            }
        ],
        aliases: ["casaconalma.com"],
        restrictions: {
            geoRestriction: {
                restrictionType: "none",
            },
        },
        defaultRootObject: "home",
        enabled: true,
        defaultCacheBehavior: {
            targetOriginId: redirectBucket.id,
            viewerProtocolPolicy: "redirect-to-https",
            allowedMethods: ["GET", "HEAD"],
            cachedMethods: ["GET", "HEAD"],
            compress: true,
            forwardedValues: {
                cookies: {
                    forward: "all",
                },
                queryString: true,
                headers: ["Host", "User-Agent"]
            },
            minTtl: 0,
            maxTtl: 86400,
            defaultTtl: 3600
        },
        priceClass: "PriceClass_100",
        viewerCertificate: {
            acmCertificateArn: certificate.arn,
            sslSupportMethod: "sni-only",
            minimumProtocolVersion: "TLSv1.2_2021"
        },
    }, {
        dependsOn: validationRecords, // Asegurar que espera los registros de validación
    });
}

