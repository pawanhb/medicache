pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID = credentials('aws_cred')
        AWS_SECRET_ACCESS_KEY = credentials('aws_cred')
        AWS_REGION = 'ap-south-1'
        ECR_PUBLIC_REGISTRY = 'public.ecr.aws/n0y2x5a0'
        ECR_REPOSITORY_URL = "${ECR_PUBLIC_REGISTRY}/medicache"
        IMAGE_TAG = 'latest'
    }

    stages {
        stage('Clean Workspace'){
            steps{
                cleanWs()
            }
        }
        stage('Checkout from Git') {
            steps {
                script{
                    git branch: 'main',
                    credentialsId : 'git_creential',
                    url: 'https://github.com/pawanhb/medicache.git'
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${ECR_REPOSITORY_URL}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Login to AWS ECR') {
            steps {
                sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
            }
        }

        stage('Push to ECR') {
            steps {
                sh "docker tag ${ECR_REPOSITORY_URL}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}"
                sh "docker push ${DOCKER_IMAGE}"
            }
        }

        stage('Cleanup') {
            steps {
                sh "docker rmi ${ECR_REPOSITORY_URL}:${env.BUILD_NUMBER} ${DOCKER_IMAGE}"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Build ${env.BUILD_NUMBER} completed successfully and pushed to ECR"
        }
        failure {
            echo "Build ${env.BUILD_NUMBER} failed"
        }
    }
}