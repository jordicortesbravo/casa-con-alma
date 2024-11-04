import * as aws from "@pulumi/aws";

export function createS3Buckets() {
    const siteName = "casaconalma.com";

    const contentBucket = new aws.s3.Bucket("content", {
        website: {
            indexDocument: "index.html",
            errorDocument: "404.html",
        },
    });

    const imagesBucket = new aws.s3.Bucket("images");

    const staticResourcesBucket = new aws.s3.Bucket("static-resources", {
        corsRules: [{
            allowedOrigins: [`https://www.${siteName}`],
            allowedMethods: ["GET", "HEAD"],
            allowedHeaders: ["*"],
            maxAgeSeconds: 3000,
        }],
    });

    return { contentBucket, imagesBucket, staticResourcesBucket };
}
