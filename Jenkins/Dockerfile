FROM jenkins/jenkins:2.387-jdk11

# ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

RUN jenkins-plugin-cli --plugins \
  docker-plugin:1.2.10 \
  docker-workflow:563.vd5d2e5c4007f \
  git:5.0.0 \
  workflow-aggregator:590.v6a_d052e5a_a_b_5 \
  junit junit:1166.va_436e268e972 \
  github:1.36.1

USER root

# Install Docker from official repo
RUN apt-get update -qq && \
    apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    apt-key fingerprint 0EBFCD88 && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" && \
    apt-get update -qq && \
    apt-get install -qqy docker-ce && \
    usermod -aG docker jenkins && \
    chown -R jenkins:jenkins $JENKINS_HOME/

RUN chmod 777 /var/run/

COPY ./jobs/ /var/jenkins_home/jobs/

USER jenkins

VOLUME [$JENKINS_HOME, "/var/run/docker.sock"]
