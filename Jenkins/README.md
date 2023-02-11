### Create a Jenkins image for efficient deployment and automation of software builds
```bash
docker build -t naturalett/jenkins:2.387-jdk11 .
```

### Configure and mount environment variables in the Jenkins container running on Docker for efficient and secure execution of builds

#### Setup Twilio Credentials
```bash
# Establish an SMS notification system using Twilio to enhance communication and improve the efficiency of the environment
mkdir -p ~/workshop-creds
cat <<EOF >> ~/workshop-creds/env-file.groovy
env.accountSid=''
env.authToken=''
env.SERVICE_SID=''
env.phoneNumber=''
EOF
```

#### Setup DockerHub Credentials
```bash
# Setup credentials for managing and distributing Docker images using DockerHub
cat <<EOF >> ~/workshop-creds/docker_login.sh
#!/bin/bash
username=''
password=''
docker login --username $username --password $password
EOF
chmod 0755 ~/workshop-creds/docker_login.sh
```

### Launch the Jenkins container on Docker for efficient automation of software builds
```bash
docker run -d \
        --name jenkins -p 8080:8080 -u root -p 50000:50000 \
        -v ~/workshop-creds:/var/workshop-creds \
        -v /var/run/docker.sock:/var/run/docker.sock \
        naturalett/jenkins:2.387-jdk11
```

### Retrieve the initial administrator password for Jenkins to gain access and start configuring the automation server
```bash
docker exec jenkins bash -c -- 'cat /var/jenkins_home/secrets/initialAdminPassword'
```

