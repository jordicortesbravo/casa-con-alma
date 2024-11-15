import * as aws from "@pulumi/aws";

export function createRoute53Records(cloudFrontDistribution: aws.cloudfront.Distribution) {
    const siteName = "casaconalma.com";

    const zone = new aws.route53.Zone("mainZone", {
        name: siteName,
    });

    new aws.route53.Record("www", {
        zoneId: zone.id,
        name: "www",
        type: "A",
        aliases: [{
            name: cloudFrontDistribution.domainName,
            zoneId: cloudFrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

    new aws.route53.Record("images", {
        zoneId: zone.id,
        name: "images",
        type: "A",
        aliases: [{
            name: cloudFrontDistribution.domainName,
            zoneId: cloudFrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

    new aws.route53.Record("static-resources", {
        zoneId: zone.id,
        name: "static-resources",
        type: "A",
        aliases: [{
            name: cloudFrontDistribution.domainName,
            zoneId: cloudFrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });
}
