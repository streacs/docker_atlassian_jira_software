require 'spec_helper'
require 'serverspec'

describe group('jira') do
  it { should exist }
end

describe user('jira') do
  it { should exist }
  it { should belong_to_group 'jira' }
  it { should have_home_directory '/home/jira' }
  it { should have_login_shell '/bin/false' }
end

describe file('/opt/jdk') do
  it { should be_directory }
  it { should be_owned_by 'root' }
  it { should be_grouped_into 'root' }
end

describe file('/home/jira') do
  it { should be_directory }
  it { should be_owned_by 'jira' }
  it { should be_grouped_into 'jira' }
end

describe file('/opt/atlassian/jira') do
  it { should be_directory }
  it { should be_owned_by 'jira' }
  it { should be_grouped_into 'jira' }
end

describe file('/var/opt/atlassian/application-data/jira') do
  it { should be_directory }
  it { should be_owned_by 'jira' }
  it { should be_grouped_into 'jira' }
end

describe file('/opt/atlassian/jira/bin/setenv.sh') do
  it { should contain 'JVM_MINIMUM_MEMORY="2g"' }
  it { should contain 'JVM_MAXIMUM_MEMORY="4g"' }
  it { should contain 'JVM_SUPPORT_RECOMMENDED_ARGS="-server"' }
end

describe file('/opt/atlassian/jira/conf/server.xml') do
  it { should contain 'proxyName="www.example.com"' }
  it { should contain 'proxyPort="443"' }
  it { should contain 'scheme="https"' }
  it { should contain 'secure="true"' }
end

describe file('/opt/atlassian/jira/atlassian-jira/WEB-INF/classes/jira-application.properties') do
  it { should contain '/var/opt/atlassian/application-data/jira' }
end