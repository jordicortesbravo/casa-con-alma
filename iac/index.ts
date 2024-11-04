import { createS3Buckets } from "./lib/s3";
import { createCloudFrontDistribution } from "./lib/cloudfront";
import { createRoute53Records } from "./lib/route53";

const { contentBucket, imagesBucket, staticResourcesBucket } = createS3Buckets();
const cloudfrontDistribution = createCloudFrontDistribution(contentBucket, imagesBucket, staticResourcesBucket);
createRoute53Records(cloudfrontDistribution);