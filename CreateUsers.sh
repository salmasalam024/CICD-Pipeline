#!/bin/bash

#Auther : Salma Salah
#Date   : 25/4/2024
# Check if script is run as root

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 
   exit 1
fi

# Create group webAdmins if it doesn't exist
grep -q "^webAdmins:" /etc/group || groupadd webAdmins

# Create DevTeam user
useradd -m -s /bin/bash DevTeam
echo "DevTeam:password" | chpasswd

# Create OpsTeam user
useradd -m -s /bin/bash OpsTeam
echo "OpsTeam:password" | chpasswd

# Add users to webAdmins group
usermod -aG webAdmins DevTeam
usermod -aG webAdmins OpsTeam

echo "Users DevTeam and OpsTeam created and added to webAdmins group successfully."