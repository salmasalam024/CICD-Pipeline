pipeline {
    agent any
    
    environment {
        GITLAB_REPO_URL = 'http://192.168.1.35/root/upload-http-and-fitch-members.git'
        ANSIBLE_HOST = 192.168.1.21
        WEB_ADMINS_GROUP = 'webAdmins'
    }
    
    stages {
        ###FIRST STAGE TO CLONE GITLAB REPO
        stage('Git Checkout') {
            steps {
                // Clone GitLab repository to Jenkins workspace
                git branch: 'main', url: "${env.GITLAB_REPO_URL}"
            }
        }



        ###SECOND STAGE TO RUN ANSIBLE PLAYBOOK   
        stage('Deploy Web Server') {
           steps {
                script {
                    ansiblePlaybook(
                       playbook: /home/salma/WebSErverSetup.yml,
                       inventory: /var/lib/jenkins/inventory
                   )
               }
           }
       }
      
     
    


        ###THIRD STAGE TO EXECUTE BASH SCRIPT
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

