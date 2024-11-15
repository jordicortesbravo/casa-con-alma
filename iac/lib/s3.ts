import * as aws from "@pulumi/aws";
import { OriginAccessIdentity } from "@pulumi/aws/cloudfront";
import * as pulumi from '@pulumi/pulumi'

export function createS3Buckets(originAccessIdentity: OriginAccessIdentity) {
    const siteName = "casaconalma.com";

    const contentBucket = new aws.s3.Bucket("content", {
        website: {
            indexDocument: "home",
            errorDocument: "404",
        },
        acl: "private"
    });

    const imagesBucket = new aws.s3.Bucket("images", {
        acl: "private"
    });

    const staticResourcesBucket = new aws.s3.Bucket("static-resources", {
        acl: "private",
        corsRules: [{
            allowedOrigins: [`https://www.${siteName}`],
            allowedMethods: ["GET", "HEAD"],
            allowedHeaders: ["*"],
            maxAgeSeconds: 3000,
        }]
    });

    // Define políticas para cada bucket
    const buckets = [{bucket: contentBucket, name: "content"}, { bucket: imagesBucket, name: "images"}, {bucket: staticResourcesBucket, name: "static-resources"}];

    buckets.forEach(bucket => {
        new aws.s3.BucketPolicy(`${bucket.name}-policy`, {
            bucket: bucket.bucket.bucket,
            policy: pulumi
            .all([bucket.bucket.bucket, originAccessIdentity.iamArn])
            .apply(([bucketName, oaiArn]) =>
                JSON.stringify({
                    Version: "2012-10-17",
                    Statement: [
                        {
                            Effect: "Allow",
                            Principal: {
                                AWS: oaiArn,
                            },
                            Action: "s3:GetObject",
                            Resource: `arn:aws:s3:::${bucketName}/*`,
                        },
                    ],
                })
            ),
        });
    });

    return { contentBucket, imagesBucket, staticResourcesBucket }
}
