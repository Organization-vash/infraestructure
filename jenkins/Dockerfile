FROM jenkins/jenkins:lts

USER root

RUN curl -fsSL -o terraform.zip https://releases.hashicorp.com/terraform/1.7.5/terraform_1.7.5_linux_amd64.zip && \
    unzip terraform.zip && \
    mv terraform /usr/local/bin/ && \
    rm terraform.zip

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY install-plugins.sh /usr/local/bin/install-plugins.sh
RUN chmod +x /usr/local/bin/install-plugins.sh && \
    /usr/local/bin/install-plugins.sh

COPY jenkins.yaml /var/jenkins_home/casc_configs/jenkins.yaml
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc_configs/jenkins.yaml

RUN groupadd -g 984 docker && usermod -aG docker jenkins

USER jenkins