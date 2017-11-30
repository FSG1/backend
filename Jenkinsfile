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
                echo 'Building...'
                sh 'mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true -Dcheckstyle.skip=true -B'
            }
        }

        stage('Test') {
            steps {
                echo 'Test...'
                sh 'mvn test -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true -B'
                sh 'mvn checkstyle:check -Dmaven.javadoc.skip=true -B'
            }
        }

        stage('Docker') {
            steps {
                echo 'Build Docker Image...'
                sh 'docker build -t 172.16.0.10:5000/backend:latest .'
            }
        }

        stage('Push') {
            steps {
                echo 'Build Docker Image...'
                sh 'docker push 172.16.0.10:5000/backend:latest'
            }
        }

        stage('Publish') {
            steps {
                echo 'Publish reports....'
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