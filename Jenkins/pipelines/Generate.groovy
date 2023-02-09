// imports
import hudson.plugins.git.*;
import jenkins.model.Jenkins
import hudson.model.ListView
import groovy.json.JsonSlurper
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

parent = Jenkins.instance
def sharedLibrariesUrl = "https://github.com/naturalett/continuous-integration.git"

scm = new GitSCM(sharedLibrariesUrl)
scm.userRemoteConfigs = scm.createRepoList(sharedLibrariesUrl, null)
scm.branches = [new BranchSpec("*/main")];

def projectNames = [
    "Artifact",
    "Build-And-Test",
    "Clone",
    "Commit",
    "Cron",
    "Deployment",
    "Full-Pipeline",
    "Junit-Test",
    "Monitoring",
    "Parallelism",
    "VCS-Any-Agent",
    "VCS-Docker-Agent",
    "Webhook"
]

for (projectName in projectNames) {
    flowDefinition = new org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition(scm, "Jenkins/pipelines/${projectName}.groovy")
    myJob = hudson.model.Hudson.instance.getJob(projectName)
    println("${projectName} is being created")
    job = new org.jenkinsci.plugins.workflow.job.WorkflowJob(parent, projectName)

    if (projectName in ["Full-Pipeline", "Monitoring"]) {
        job.definition = new CpsFlowDefinition(
          """
node {
      git branch: 'main', url: 'https://github.com/naturalett/continuous-integration.git'
      load './Jenkins/pipelines/${projectName}.groovy'
}
          """
    )
    } else {
        job.definition = flowDefinition
    }
    parent.reload()
}

