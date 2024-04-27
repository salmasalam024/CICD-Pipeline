pipeline {
    agent any
    
    environment {
        GITLAB_REPO_URL = 'http://192.168.1.35/root/upload-http-and-fitch-members.git'
        ANSIBLE_HOST = 192.168.1.21
        WEB_ADMINS_GROUP = 'webAdmins'
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                // Clone GitLab repository to Jenkins workspace
                git branch: 'main', url: "${env.GITLAB_REPO_URL}"
            }
        }
        
        stage('Ansible Deploy') {
            steps {
                script {
                    try {
                        // Execute Ansible playbook to install and configure Apache HTTP Server
                        sh "ansible-playbook -i ${env.ANSIBLE_HOST}, /home/salma/WebSErverSetup.yml"
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

    ##Another code for Deploying Web Server Stage
    #    stage('Deploy Web Server') {
    #        steps {
    #            script {
    #                ansiblePlaybook(
    #                    playbook: /home/salma/WebSErverSetup.yml,
    #                    inventory: /var/lib/jenkins/inventory
    #                )
    #            }
    #        }
    #    }
    #  }
        
        stage('Capture Group Members') {
            steps {
                script {
                    // Execute GroupMembers.sh script to capture group members
                    def groupMembers = sh(script: "/home/salma/GroupMEmbers.sh ${env.WEB_ADMINS_GROUP}", returnStdout: true).trim()
                    echo "Group Members in '${env.WEB_ADMINS_GROUP}' group: ${groupMembers}"
                }
            }
        }
    }
    
    post {
  
        failure {
            // Send email notification on pipeline failure
            emailext subject: "Pipeline Failed - ${env.JOB_NAME}",
            to: salmasalam024@gmail.com
            body: "Pipeline failed due to: ${currentBuild.result}\n\n" +
                      "Group Members in '${env.WEB_ADMINS_GROUP}' group:\n${groupMembers}\n\n" +
                      "Date of pipeline execution: ${currentBuild.getTime().toString()}",
                       recipientProviders: [[$class: 'DevelopersRecipientProvider']]
        }
    }
}
