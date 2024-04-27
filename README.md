
---
# Documentation on Ansible playbook and Jenkins pipeline configuration. 

## Workflow Overview

1. **Code Management**: Developers push changes to the GitLab repository hosted on the second virtual machine. This repository contains the Ansible playbook (`WebServerSetup.yml`) and other automation scripts.

2. **Pipeline Trigger**: Jenkins monitors the GitLab repository for new commits or changes.

3. **Pipeline Execution**:
   - Upon detecting a code commit, Jenkins triggers a pipeline job.
     - The Jenkins pipeline job includes steps to:
       - Clone the GitLab repository containing(`WebServerSetup.yml`) and onto the Jenkins server.
       - Use Ansible (installed on the Jenkins server) to execute the(`WebServerSetup.yml`) playbook against the third virtual machine 
         (target host).
       - Capture the output of the (`GroupMembers.sh`) script to retrieve information about the "webAdmins" group members.
       - Send email notifications with relevant details (e.g., pipeline status, failure reasons, group members) upon completion or 
         failure of the pipeline.

4. **Email Notifications**:
   - Email notifications are sent upon failure of the pipeline.
   - Notifications include pipeline status, group members' information, and the date of execution.
     - Git Plugin
     - Email Extension Plugin (for sending email notifications)

## Steps

## First: Jenkins Server Instalation on First Virtual Machine
 ### 1. Install JAVA 
 **Note: JAVA Version MUST be the latest version or Jenkins will not operate this was a problem i faced during installation and solved it by installing java 11 instead of java-1.8** 
 ### 2. Add Jenkins Repository
 ### 3. Install Jenkins and Start Jenkins Service
 ### 4. Adjust Firewall Rules to allow traffic
```bash
sudo yum install java-11-openjdk-devel -y
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
sudo yum install jenkins -y
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo firewall-cmd --zone=public --add-port=8080/tcp --permanent
sudo firewall-cmd --reload
```
Now Jenkins server is ready

## Second: Private GitLab Instance Installation on Second vitual machine (IP Address: 192.168.1.35)
### 1. Install Dependencies
### 2. Configure Postfix (Mail Server)
### 3. Install GitLab
**Note: At this step you must provide a domain to be able to access gitlab using it this was alsoaproblem ifaced duringinstalling the gitlab instance and solved it by adding my private ip address but another problem is that this address is configured ('dhcp') so i needed to reconfigure it be to static so it won't be changed at powering of the machine using following steps:** 
```bash
ifconfig
route -n
cd /etc/sysconfig/network-scripts
```
**Inside this file you have to configure** 
 -  POOTPROTO= static
 -  IPADDR= put your ipaddress
 -  NETMASK= put the networkmask
 -  GATEWAY= put the gateway
   
**By doing this steps ip address now will be static and be used in gitlab installation and to access gitlab instance**

### 4. Reconfigure GitLab
```bash
sudo yum install -y curl policycoreutils openssh-server openssh-clients postfix
sudo systemctl enable postfix
sudo systemctl start postfix
curl -sS https://packages.gitlab.com/install/repositories/gitlab/gitlab-ce/script.rpm.sh | sudo bash
sudo EXTERNAL_URL="http://your-gitlab-domain.com" yum install -y gitlab-ce
sudo gitlab-ctl reconfigure

```
### 5. Get the GitLab root password to be able to login
```bash
cat /etc/gitlab/initial-root-password
```

## Third: Jenkins Integration with GitLab**
### 1.Install Required Plugins 
GitLab
GitLab Authentication
GitLab API
### 2.Create an API personal Access Tokenin GitLab and add it to my credentials in Jenkins 
### 3.Setup GitLab Connection in Jenkins by inserting GitLab instance URL (static ip address) and the API token
### 4.Inatiating SSH Connection betweeen Jenkins and GitLab by craeting Public and Private key in my jenkins server 
```bash
ssh-keygen
cd.ssh/
ls
cat id-rsa.pub
```
### 5.Copy this public key to GetLab and insert it into SSH Keys > Add Public key 
### 6.Into Jenkins copy the private key
```bash
cat id-rsa
```
### Add the private key into gloabal Credentials

## Fourth: Git Installation and Configuration on Second vitual machine and Push files (`WebServerSetup.yml`) and (`GroupMembers.sh`)  into GitLab Repositories

```bash
sudo yum install git
git config --global user.name "my Name"
git config --global user.email "my email address"
```
**Make a repository in my gitlab and got the URL to add it as an Origin**
```bash
git add WebServerSetup.yml
git add GroupMembers.sh
git commit -m "update http and fitch members"
git remote add origin "repository URL"
git push -u origin master 
```

## Fifth: On Third Virtual Machine (Target VM IPAddress: 192.168.1.21)
### 1.  Write a bash script "GroupMembers.sh to create users named "DevTeam" and "OpsTeam" on  Assign these users to a group "webAdmins"
```bash
chmod +x (`GroupMembers.sh`)
./(`GroupMembers.sh`) 
```
### 2. Make static ip address to the third virtual machine to add this ip address in Ansible Inventory 
```bash
ifconfig
route -n
cd /etc/sysconfig/network-scripts
```
**Inside this file you have to configure** 
 -  POOTPROTO= static
 -  IPADDR= put your ipaddress
 -  NETMASK= put the networkmask
 -  GATEWAY= put the gateway
   
**By doing this steps ip address now will be static and be used in ansible Inventory file in the control machine (VM1) as the ip address of the target machine (VM3)**
### 3. Make an SSH user and password that ansible will use to establish SSH Connection
 **Note:This user will be used in ansible Inventory file to integrate ansible in the controlmachine (VM1) with Ansibleplaybook (`WebServerSetup.yml`) on the target machine (VM3)** 
 

## Sixth: Ansible Instalation on first (Control) virtual machine Jenkins Server (VM1) and Integration with Jenkins
### 1. Install ansible
```bash
sudo yum install ansible
```
### 2. Create a key pair in (VM1) 
```bash
ssh-keygen
cd.ssh/
ls
cat id-rsa.pub
```
**Copy this public key to target machine (VM3) and save it's path to add it in the Ansible Inventory** 

### 3. Configure Ansible Inventory
```bash
sudo  vim /var/lib/jenkins/inventory
```
**Inside ansible inventory file you have to define the target host that ansible will manage (VM3)** 
 -  VM3 IP Address (192.168.1.21)
 -  ansible_ssh_user=salma
 -  ansible_ssh_private_key_file=/path/to/your/private_key
   
### 4. Integrate Ansible with Jenkins 
 -  Install Ansible Plugin
 -  Add Ansible Inventory to Jenkins Pipeline


## Seventh: Configuration of Jenkins pipeline and testing the CICD pipelibe Execution
   Configure Jenkins Email Notifications*:
   - Install and configure the Jenkins Email Extension plugin if not already installed.
   - Configure the SMTP server settings in Jenkins to enable email notifications.








