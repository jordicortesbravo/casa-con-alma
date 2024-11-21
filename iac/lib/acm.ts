import * as aws from "@pulumi/aws";

export function createAcmCertificate() {
    // Crear un certificado ACM para el dominio y subdominios
    return new aws.acm.Certificate("casa-con-alma-certificate", {
        domainName: "casaconalma.com", 
        subjectAlternativeNames: [
            "www.casaconalma.com",        // Subdominio www
        ],
        validationMethod: "DNS",  // Usar validación DNS
    }, {provider: new aws.Provider("aws-provider", {region: "us-east-1"})});
}
