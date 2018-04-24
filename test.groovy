#!groovy
import groovy.json.JsonSlurper


def jsonSlurper = new JsonSlurper()

def api_token='xxxxx'
def get = new URL("https://api.distelli.com/ipcrm/apps/puppet_webapp/envs?apiToken=${api_token}").openConnection();
def getRC = get.getResponseCode();
println(getRC);
if(getRC.equals(200)) {
//    println(get.getInputStream().getText());
    def object = jsonSlurper.parseText(get.getInputStream().getText());
    print object.keySet();
}
