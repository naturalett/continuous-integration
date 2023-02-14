// imports
import hudson.plugins.git.*;
import jenkins.model.Jenkins
import hudson.model.ListView
import groovy.json.JsonSlurper
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

properties([
    parameters([
        string(defaultValue: 'https://github.com/naturalett/continuous-integration.git', description: 'Configure your repository pipelines', name: 'repository')
    ])
])

parent = Jenkins.instance

scm = new GitSCM(params.repository)
scm.userRemoteConfigs = scm.createRepoList(params.repository, null)
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
    "Monitoring-Phone",
    "Monitoring-WhatsApp",
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

    if (projectName in ["Full-Pipeline", "Monitoring-Phone", "Monitoring-WhatsApp"]) {
        job.definition = new CpsFlowDefinition(
          """
node {
      git branch: 'main', url: '${params.repository}'
      load './Jenkins/pipelines/${projectName}.groovy'
}
          """
    )
    } else {
        job.definition = flowDefinition
    }
    parent.reload()
}

