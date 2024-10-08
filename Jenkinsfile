pipeline {
    agent any
    stages {
        stage ('Backend Build') {
            steps{
                bat 'mvn clean package -DskipTests=true'
            }
        }
        stage ('Unit Tests') {
            steps{
                // O clean já foi feito acima
                bat 'mvn test'
            }
        }
        stage ('Solar Analysis') {
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps{
                withSonarQubeEnv('SONAR_LOCAL') {
                   // Trouxe do próprio scanner(também está no deployback)
                   bat "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=DeployBack -Dsonar.host.url=http://localhost:9000 -Dsonar.login=8331e97fd6a5024c2b78c8b0e5f07118c1fb3d06 -Dsonar.java.binaries=target -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java"     
                }
            }
        }
        stage ('Backend Deploy') {
            steps{
                deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001')], contextPath: 'tasks-backend', onFailure: false, war: 'target/tasks-backend.war'
            }
        }
        stage ('API Tests') {
            steps{
                dir('api-test') {
                    git branch: 'main', url: 'https://github.com/JoseMS96/tasks-api-test'
                    bat 'mvn test'
                }
            }
        }
        stage ('Frontend Deploy') {
            steps{
                dir('frontend'){
                    git branch: 'master', url: 'https://github.com/JoseMS96/tasks-frontend'
                    // Aqui é em outro diretório, então tem que fazer o clean denovo
                    bat 'mvn clean package'
                    deploy adapters: [tomcat8(credentialsId: 'TomcatLogin', path: '', url: 'http://localhost:8001')], contextPath: 'tasks', onFailure: false, war: 'target/tasks.war'
                }
            }
        }
        stage ('Functional Tests') {
            steps{
                dir('functional-test') {
                    git branch: 'main', url: 'https://github.com/JoseMS96/tasks-functional-tests'
                    bat 'mvn test'
                }
            }
        }
        stage ('Prod Deploy') {
            steps{
                bat 'docker-compose build'
                // Libera o terminal para o jenkins saber que finalizou o up 
                bat 'docker-compose up -d'
            }
        }
        stage ('Health Check') {
            steps{
                sleep(7)
                dir('functional-test') {
                    bat 'mvn verify -Dskip.surefire.tests'
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml, api-test/target/surefire-reports/*.xml, functional-test/target/surefire-reports/*.xml, functional-test/target/failsafe-reports/*.xml'
            archiveArtifacts artifacts: 'target/tasks-backend.war, frontend/target/tasks.war', followSymlinks: false, onlyIfSuccessful: true
        }
        unsuccessful {
            emailext attachLog: true, body: 'See the attached log', subject: 'Build $BUILD_NUMBER has failed', to: 'melo.jeduardo+jenkins@gmail.com'
        }
        fixed {
            emailext attachLog: true, body: 'See the attached log', subject: 'Build is healthy', to: 'melo.jeduardo+jenkins@gmail.com'
        }
    }
}