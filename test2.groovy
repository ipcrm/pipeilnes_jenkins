#!groovy
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def api_token ='XXXX' //ENV or secret

def minurl = "https://api.distelli.com"
def minargs = "ipcrm/envs/puppet_webapp_int/deploy?apiToken=${api_token}"

def payload = [:]
payload['release_version'] = 'v124'
payload['description'] = 'Deploying version v124'

def response = restPost(minurl,minargs,payload)
print response

def restPost (baseurl,args,payload) {
  def jsonSlurper = new JsonSlurper()
  try {
    def fullurl = "${baseurl}/${args}"
    def post = new URL(fullurl).openConnection();
    post.setRequestMethod("POST")
    post.setDoOutput(true)
    post.setRequestProperty("Content-Type", "application/json")
    post.getOutputStream().write(JsonOutput.toJson(payload).getBytes("UTF-8"));
    def postRC = post.getResponseCode();
    if(postRC.equals(200)) {
      def object = jsonSlurper.parseText(post.getInputStream().getText());
      return object
    }else{
      throw "POST to ${baseurl} failed! Response code ${postRC}"
    }
  } catch (Exception e) {
    throw e
  }
}
