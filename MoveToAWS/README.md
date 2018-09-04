## Steps to move application to AWS ECS using a Docker image
* Build a docker image using spotify maven plugin: Update the pom.xml with the plugin details and remember to change the image name.

  ```
  mvn clean
  mvn package
  mvn docker:build
  docker images
  ```
* Upload the docker Image to Amazon ECR repository. AWS ECR will provide the commands to be run in the console.
* **Error resolution** when Docker push to ECS: No credential auth error
  * Run the below command: eval $(aws ecr get-login --no-include-email | sed 's|https://||') and try again.
  * You have authenticated to push to a repository on the wrong AWS account. Do aws configure again with the correct account and execute the push commands.
* Create a security group for the ECS migration: 
  ```
  aws ec2 create-security-group --group-name SpringBootAppECS --description "ECS security group for the spring boot app"
  ```
* Create a cloud watch log group to check application logs:
  ```
  aws logs create-log-group --log-group-name currency-exchange-service-cloudwatch --region us-east-2
  ```
* Amazon ECS create a cluster using EC2 linux and networking and proceee to finish the other config values like desired instances, VPC and subnet details. Remember to choose a key pair as this is required to SSH to EC2 instance created by the container.
* The above step will create an ECS instance with EC2 instances up and running as per the desired configuration.
* Create a new task defintion to run the application using the above created instances. This is where the docker image details from the ECR needs to be provided.
* The task definition could be configured via the below JSON in the console or the CLI.
    ```
  {
     "family":"currency-exchange-service",
     "containerDefinitions":[
        {
           "name":"web",
           "image":"171551218701.dkr.ecr.us-east-2.amazonaws.com/currency-exchange-service:latest",
           "cpu":128,
           "memoryReservation":128,
           "portMappings":[
              {
                 "containerPort":8000,
                 "hostPort":8000
              }
           ],
           "logConfiguration":{
              "logDriver":"awslogs",
              "options":{
                 "awslogs-group":"currency-exchange-service-cloudwatch",
                 "awslogs-region":"us-east-2",
                 "awslogs-stream-prefix":"currency-exchange-service-cloudwatch"
              }
           },
           "command":[

           ],
           "essential":true
        }
     ]
   }
      
    ```
    
    ```
    aws ecs register-task-definition --cli-input-json file://currency-exchange-service-task-definition.json
    
    ```
 * Once the task definition is in Active state, go ahead and associated this task to the cluster created previously.
 * Navigate to cluster -> Task tab -> Run new task and complete the configuration.
 * Hit the application URL and then view the logs in cloud watch logs group. A new stream will be created per application instance.
