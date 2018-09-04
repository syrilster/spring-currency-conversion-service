## Steps to move application to AWS ECS using a Docker image
* Build a docker image using spotify maven plugin: Update the pom.xml with the plugin details and remember to change the image name.

  ```
  mvn clean
  mvn package
  mvn docker:build
  docker images
  ```
* Upload the docker Image to Amazon ECR repository. AWS ECR will provide the commands to be run in the console.
* Docker push to ECS: No credential auth error
  * Run the below command: eval $(aws ecr get-login --no-include-email | sed 's|https://||') and try again.
  * You have authenticated to push to a repository on the wrong AWS account. Do aws configure again with the correct account and execute the push commands.
