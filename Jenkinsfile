pipeline {
    agent none
    stages {
        stage('Platforms') {
            steps {
                parallel unix: {
                    node("${env.NIX_LABEL}") {
                        stage('Unix Build') {
                            checkout scm
                            withEnv(["JAVA_HOME=${tool('JDK 1.8.0_144')}", "PATH+MAVEN=${tool('Maven 3.5.0')}/bin:${env.JAVA_HOME}/bin"]) {
                                sh "mvn clean install jacoco:report -B -U -e -fae -V -P run-its,jenkins -Dsurefire.useFile=false -Dfailsafe.useFile=false -Dintegration-test-port=8084   \\\"-Djdk.home=${tool('JDK 9 b181')}\\\""
                            }
                            jacoco changeBuildStatus: false, execPattern: '**/*.exec'
                            post {
                                always {
                                    junit healthScaleFactor: 0.0, allowEmptyResults: true, keepLongStdio: true, testResults: '**/surefire-integration-tests/target/failsafe-reports/**/*.xml,**/surefire-integration-tests/target/surefire-reports/**/*.xml,**/maven-*/target/surefire-reports/**/*.xml,**/surefire-*/target/surefire-reports/**/*.xml,**/common-*/target/surefire-reports/**/*.xml'
                                }
                            }
                        }
                    }
                },
                windows: {
                    node("${env.WIN_LABEL}") {
                        stage('Windows Build') {
                            checkout scm
                            withEnv(["JAVA_HOME=${tool('JDK 1.8_121 (Windows Only)')}", "PATH+MAVEN=${tool('Maven 3.5.0 (Windows)')}\\bin;${env.JAVA_HOME}\\bin"]) {
                                bat "mvn clean install jacoco:report -B -U -e -fae -V -P run-its,jenkins -Dsurefire.useFile=false -Dfailsafe.useFile=false -Dintegration-test-port=8084"
                            }
                            jacoco changeBuildStatus: false, execPattern: '**/*.exec'
                            post {
                                always {
                                    junit healthScaleFactor: 0.0, allowEmptyResults: true, keepLongStdio: true, testResults: '**/surefire-integration-tests/target/failsafe-reports/**/*.xml,**/surefire-integration-tests/target/surefire-reports/**/*.xml,**/maven-*/target/surefire-reports/**/*.xml,**/surefire-*/target/surefire-reports/**/*.xml,**/common-*/target/surefire-reports/**/*.xml'
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    options {
        buildDiscarder(logRotator(numToKeepStr:'3'))
        timeout(time: 10, unit: 'HOURS')
    }
}
