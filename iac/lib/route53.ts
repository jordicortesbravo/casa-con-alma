import * as aws from "@pulumi/aws";
import * as pulumi from '@pulumi/pulumi';

const siteName = "casaconalma.com";

export function createZone() {
    return new aws.route53.Zone("main-zone", {
        name: siteName,
    });
}

export function createValidationRecords(zone: aws.route53.Zone, certificate: aws.acm.Certificate): pulumi.Output<aws.route53.Record[]> {
    const validationRecords = certificate.domainValidationOptions.apply(domainValidationOptions => {
        return domainValidationOptions.map((option, index) => {
            return new aws.route53.Record(`dns-validation-record-${index}`, {
                zoneId: zone.id,
                name: option.resourceRecordName,
                type: option.resourceRecordType,
                ttl: 60,
                records: [option.resourceRecordValue],
            });
        });
    });

    return validationRecords
}

export function createRoute53Records(zone: aws.route53.Zone, cloudFrontDistribution: aws.cloudfront.Distribution) {

    new aws.route53.Record("root", {
        zoneId: zone.id,
        name: siteName,  // Sin "www" para el dominio raíz
        type: "A",
        aliases: [{
            name: cloudFrontDistribution.domainName,
            zoneId: cloudFrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

    new aws.route53.Record("dns-record-www", {
        zoneId: zone.id,
        name: "www",
        type: "A",
        aliases: [{
            name: cloudFrontDistribution.domainName,
            zoneId: cloudFrontDistribution.hostedZoneId,
            evaluateTargetHealth: false,
        }],
    });

}
