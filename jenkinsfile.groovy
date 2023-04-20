pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                git branch: 'main', url: 'https://github.com/Mallik-24/my-ChatBot.git'
                sh 'pip install rasa'
                sh 'rasa train'
                sh 'rasa test'
            }
        }

        stage('Dockerize') {
            steps {
                sh 'docker build -t my-chatbot .'
                sh 'docker tag my-chatbot mallik2001/my-chatbot:latest'
                sh 'docker push mallik2001/my-chatbot:latest'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'my-creds-id', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD'
                    sh 'kubectl apply -f kubernetes/my-chatbot-deployment.yaml'
                    sh 'kubectl apply -f kubernetes/my-chatbot-service.yaml'
                }
            }
        }
    }
}
