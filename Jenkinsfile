pipeline {
    agent any
    stages {
        stage ('Build Backend') {
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
    }
}