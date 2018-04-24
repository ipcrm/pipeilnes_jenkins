#!groovy
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

DISTELLI_USERNAME = 'ipcrm'
DISTELLI_API_URL  = "https://api.distelli.com/$DISTELLI_USERNAME"
DISTELLI_APP_NAME = "pipeilnes_jenkins"
PIPELINES_API_TOKEN ='ibmmox8a2mspxs5lzh3ozhwe9xyq0smslsmmg' //BAD!



node('pipelines') {
  checkout(scm).each { k,v -> env.setProperty(k, v) }
  stage('build stuff'){
    set_env_stuff(config)
    try {
      step {
        set_env_stuff()
        create_push_event()
        def build_id = create_build_event

        // COMPILE/PACKAGE/WHATEVER
        sh('distelli push -save-release release_version.out')
        // END Compile

        update_build_status(build_id,'Success')
      }
    } catch (all) {

    }

  }
}

def set_env_stuff() {
  env.GIT_AUTHOR_NAME = sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
  def DISTELLI_USERNAME = config['user']
  def DISTELLI_API_URL = config['api_url']
  def DISTELLI_APP_NAME = config['app_name']
  def DISTELLI_API_TOKEN = config['api_token']
  def DISTELLI_CHANGE_TITLE = sh(returnStdout: true, script: 'git --no-pager log --pretty=format:"%s" -1')
  def DISTELLI_BUILD_URL= env.BUILD_URL
  def DISTELLI_CHANGE_AUTHOR = env.GIT_AUTHOR_NAME
  def DISTELLI_CHANGE_AUTHOR_DISPLAY_NAME = env.GIT_AUTHOR_NAME
  def DISTELLI_CHANGE_ID = env.GIT_COMMIT
  def DISTELLI_BRANCH_NAME = env.GIT_BRANCH.split('/')[1]
  def DISTELLI_CHANGE_TARGET = env.GIT_URL
  def DISTELLI_CHANGE_URL = "${env.GIT_URL}/commits/${DISTELLI_CHANGE_ID}"
}

def create_push_event(config){
  stage('Create Push Event'){
    set_env(config)
    def pipeargs = "apps/${DISTELLI_APP_NAME}/events/pushEvent?apiToken=${PIPELINES_API_TOKEN}"
    def data = [:]
    data['author_username'] = DISTELLI_CHANGE_AUTHOR
    data['author_name'] = DISTELLI_CHANGE_AUTHOR_DISPLAY_NAME
    data['commit_msg'] = DISTELLI_CHANGE_TITLE 
    data['commit_url'] = DISTELLI_CHANGE_URL
    data['repo_url']  = DISTELLI_CHANGE_TARGET
    data['commit_id'] = DISTELLI_CHANGE_ID 
    data['repo_owner'] = 'ipcrm' 
    data['repo_name'] = 'pipelines_jenkins'
    data['branch'] = DISTELLI_BRANCH_NAME

    def test = pushData('PUT',config['api_url'],pipeargs,data)
    echo test['event_id']
    return test['event_id']
  }
}

def create_build_event(config){
    stage('Create Build Event'){
        def DISTELLI_NOW = sh(returnStdout: true, script: 'date -u +%Y-%m-%dT%H:%M:%S.0Z').trim()
        def builddata = [:]
        builddata["build_status"] = 'running'
        builddata["build_start"] = DISTELLI_NOW
        builddata["build_provider"] = 'jenkins'
        builddata["build_url"] = DISTELLI_BUILD_URL
        builddata["repo_url"] = DISTELLI_CHANGE_TARGET 
        builddata["commit_url"] = DISTELLI_CHANGE_URL
        builddata["author_username"] = DISTELLI_CHANGE_AUTHOR
        builddata["author_name"] = DISTELLI_CHANGE_AUTHOR_DISPLAY_NAME
        builddata["commit_msg"] = DISTELLI_CHANGE_TITLE
        builddata["commit_id"] = DISTELLI_CHANGE_ID
        builddata["branch"] = DISTELLI_BRANCH_NAME
      builddata["parent_event_id"] = PIPELINES_PUSH_EVENT_ID
      
      def pipeargs = "apps/${DISTELLI_APP_NAME}/events/buildEvent?apiToken=${PIPELINES_API_TOKEN}"
      return pushData('PUT',DISTELLI_API_URL,pipeargs,builddata)['event_id']
  }
}

def config = [:]

node('pipelines') {

  config['user'] = 'ipcrm'
  config['api_url']  = "https://api.distelli.com/${config['user']}"
  config['app_name'] = "pipeilnes_jenkins"
  config['api_token'] = 'ibmmox8a2mspxs5lzh3ozhwe9xyq0smslsmmg' //BAD!

  stage('setup') {
    checkout(scm).each { k,v -> env.setProperty(k, v) }
    pipelines = load("lib/pipelines.groovy")
    pipelines.set_env(config)
  }

  stage('build stuff'){
    config['push_id'] = pipelines.create_push_event(config)
    config['build_id'] = pipelines.create_build_event(config)

    // COMPILE/PACKAGE/WHATEVER
    sh('distelli push -save-release release_version.out')
    // END Compile
    pipelines.update_build_status(config['build_id'],'Success',config)
  }
}
