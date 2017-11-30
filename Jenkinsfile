pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK8'
    }

    stages {
        stage ('Checkout') {
            steps {
                checkout scm
            }
        }

        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true -Dcheckstyle.skip=true -B'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true -B'
                sh 'mvn checkstyle:check -Dmaven.javadoc.skip=true -B'
            }
        }

        stage('Docker') {
            steps {
                sh "docker build -t 172.16.0.10:5000/backend:la${env.BRANCH_NAME}test ."
            }
        }

        stage('Push') {
            steps {
                sh "docker push 172.16.0.10:5000/backend:${env.BRANCH_NAME}"
            }
        }

        stage('Publish') {
            steps {
                junit 'target/surefire-reports/**/*.xml'
                jacoco(execPattern: '**/**.exec')
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
