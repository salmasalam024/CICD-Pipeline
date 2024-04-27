
---

# Jenkins Pipeline for Ansible Deployment and Group Member Capture

This Jenkins pipeline automates the deployment of Ansible playbooks triggered by updates in a GitLab repository. Additionally, it captures information about group members for notification purposes upon pipeline completion or failure.

## Workflow Overview

1. **Code Management**: Developers push changes to the GitLab repository hosted on the second virtual machine. This repository contains the Ansible playbook (`WebServerSetup.yml`) and other automation scripts.

2. **Pipeline Trigger**: Jenkins monitors the GitLab repository for new commits or changes.

3. **Pipeline Execution**:
   - Jenkins triggers a pipeline job upon detecting changes in the GitLab repository.
   - The pipeline job includes stages to clone the repository, deploy the Ansible playbook to a target host (third virtual machine), and capture group member information using a custom script (`GroupMembers.sh`).

4. **Email Notifications**:
   - Email notifications are sent upon completion or failure of the pipeline.
   - Notifications include pipeline status, group members' information, and the date of execution.

## Prerequisites

- Jenkins server installed and configured on the first virtual machine.
- Ansible installed and configured on the Jenkins server.
- GitLab repository accessible from the Jenkins server.

## Pipeline Setup

1. **Install Required Jenkins Plugins**:
   - Install the following Jenkins plugins:
     - Pipeline Plugin
     - Git Plugin
     - Email Extension Plugin (for sending email notifications)

2. **Configure Jenkins Credentials**:
   - Ensure Jenkins has appropriate credentials to access the GitLab repository.

3. **Define Jenkins Pipeline**:
   - Write stages of the pipeline.
