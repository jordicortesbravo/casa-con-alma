import * as aws from "@pulumi/aws";
import * as pulumi from '@pulumi/pulumi';

export function createCloudFrontDistribution(
    contentBucket: aws.s3.Bucket,
    certificate: aws.acm.Certificate,
    validationRecords: pulumi.Output<aws.route53.Record[]>
) {

    const cloudfrontDistribution = new aws.cloudfront.Distribution("website-cloudfront-distribution", {
        origins: [
            {
                originId: contentBucket.id,
                domainName: contentBucket.websiteEndpoint, // Endpoint del sitio web
                customOriginConfig: {
                    httpPort: 80,
                    httpsPort: 443,
                    originProtocolPolicy: "http-only", // Los endpoints de sitio web no soportan HTTPS
                    originSslProtocols: ["TLSv1.2"],
                },
            }
        ],
        aliases: ["www.casaconalma.com"],
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

    return cloudfrontDistribution;
}