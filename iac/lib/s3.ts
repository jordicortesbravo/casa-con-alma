import * as aws from "@pulumi/aws";
import * as pulumi from '@pulumi/pulumi'

const callerIdentity = aws.getCallerIdentity();

export function createS3Buckets() {
    const siteName = "casaconalma.com";

    const bucket = new aws.s3.Bucket(`www.${siteName}`, {
        bucket: `www.${siteName}`,
        website: {
            indexDocument: "home",
            errorDocument: "404",
        },
    });

    new aws.s3.BucketPublicAccessBlock(`www.${siteName}-public-access-block`, {
        bucket: bucket.id,
        blockPublicAcls: false,
        blockPublicPolicy: false,
        ignorePublicAcls: false,
        restrictPublicBuckets: false,
    });


    new aws.s3.BucketPolicy(`www.${siteName}-bucket-policy`, {
        bucket: bucket.bucket,
        policy: bucket.bucket.apply(bucket => JSON.stringify({
            Version: "2012-10-17",
            Statement: [
                {
                    Effect: "Allow",
                    Principal: "*",
                    Action: "s3:GetObject",
                    Resource: `arn:aws:s3:::${bucket}/*`,
                },
            ],
        })),
    });
    return bucket;
}
