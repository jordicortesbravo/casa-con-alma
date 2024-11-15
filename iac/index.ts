import { createS3Buckets } from "./lib/s3";
import { createCloudFrontDistribution } from "./lib/cloudfront";
import { createRoute53Records } from "./lib/route53";
import * as aws from "@pulumi/aws";

// Crea la Origin Access Identity para CloudFront
const originAccessIdentity = new aws.cloudfront.OriginAccessIdentity("websiteOriginAccessIdentity");

// Crea los buckets de S3 con la política de acceso que restringe el acceso directo
const { contentBucket, imagesBucket, staticResourcesBucket } = createS3Buckets(originAccessIdentity);

// Crea la distribución de CloudFront usando los buckets y la OAI
const cloudfrontDistribution = createCloudFrontDistribution(contentBucket, imagesBucket, staticResourcesBucket, originAccessIdentity);

// Configura los registros de Route 53
createRoute53Records(cloudfrontDistribution);
