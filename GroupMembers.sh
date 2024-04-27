#!/bin/bash

# Fetch members of the 'webAdmins' group
group_name="webAdmins"

# Check if the group exists
getent group "$group_name" &>/dev/null
if [[ $? -eq 0 ]]; then
    # Extract and display group members
    members=$(getent group "$group_name" | cut -d: -f4)
    echo "Members of group '$group_name':"
    echo "$members"
else
    echo "Group '$group_name' does not exist."
fi