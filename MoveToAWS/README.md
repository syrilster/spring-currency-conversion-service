## Steps to move application to AWS using a Docker image
* Upload the docker Image to Amazon ECR repository. AWS ECR will provide the commands to be run in the console.
* Docker push to ECS: No credential auth error
  * Run the below command: eval $(aws ecr get-login --no-include-email | sed 's|https://||') and try again.
  * You have authenticated to push to a repository on the wrong AWS account. Do aws configure again with the correct account and execute the push commands.
