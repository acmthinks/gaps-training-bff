node {
   def mvnHome
   stage('Pull Source Code') { // for display purposes
      // Get some code from a GitHub repository
      git 'https://github.com/acmthinks/gaps-training-bff.git'
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
      mvnHome = tool 'M3'
   }
   stage('Build & Package') {
      // Run the maven build
      if (isUnix()) {
         sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
      } else {
         bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
      }
   }
   stage('Unit Test') {
      junit '**/target/surefire-reports/TEST-*.xml'
      archive 'target/*.jar'
   }
   stage('Deploy') {
       pushToCloudFoundry cloudSpace: 'dev', credentialsId: '9b6d7cba-be87-436f-a6ff-689dbff29d62', organization: 'acm@us.ibm.com', pluginTimeout: 300, target: 'https://api.ng.bluemix.net'
   }
}
