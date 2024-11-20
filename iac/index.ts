import { createAcmCertificate } from "./lib/acm";
import { createCloudFrontDistribution } from "./lib/cloudfront";
import { createRoute53Records, createValidationRecords, createZone } from "./lib/route53";
import { createS3Buckets } from "./lib/s3";

const zone = createZone();
const certificate = createAcmCertificate();
const validationRecords = createValidationRecords(zone, certificate);

const  contentBucket = createS3Buckets();

const cloudfrontDistribution = createCloudFrontDistribution(contentBucket, certificate, validationRecords);

createRoute53Records(zone, cloudfrontDistribution);
