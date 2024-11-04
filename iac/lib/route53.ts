import * as aws from "@pulumi/aws";

export function createRoute53Records(cloudfrontDistribution: aws.cloudfront.Distribution) {
    const siteName = "casaconalma.com";

    const zone = new aws.route53.Zone("mainZone", {
        name: siteName,
    });

    new aws.route53.Record("www", {
        zoneId: zone.id,
        name: "www",
        type: "A",
        aliases: [{
            name: cloudfrontDistribution.domainName,
            zoneId: cloudfrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

    new aws.route53.Record("images", {
        zoneId: zone.id,
        name: "images",
        type: "A",
        aliases: [{
            name: cloudfrontDistribution.domainName,
            zoneId: cloudfrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

    new aws.route53.Record("static-resources", {
        zoneId: zone.id,
        name: "static-resources",
        type: "A",
        aliases: [{
            name: cloudfrontDistribution.domainName,
            zoneId: cloudfrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });
}
