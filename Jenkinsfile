@Library('CxLibrary') _

mavenAppPipeline(
    // customParameters
    [
        ignoreTestFailure: 'true', 
        junitTestResults: 'app/target/surefire-reports/*.xml',
        dockerRepositoryName: 'cx-notification-services',
        dockerBuildParameters: '-f ./app/docker/Dockerfile ./app',
        deploySnapshots: false,
        confluenceKey: 'CX2S',
        confluenceAncestorId: '871334382',
        confluenceCredentialsId: 'confluence-team',
        //sonarProjectKey: 'cx-services-notifications',
        unreleasedEnvironment: [
            enabled: false,
            awsCredentialsId: 'AWS-Dev',
            environmentRegion: 'eu-west-1',
            ecsService: [
                clusterName: 'dev-layer3-sharedservices-01',
                serviceName: 'dev-service-cx-notification-services'
            ]
        ]
    ]
)