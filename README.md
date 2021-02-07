<p align="center">
  <a href="https://servobot.info">
    <img src="src/main/resources/static/images/MoosersBot.png" alt="Logo">
  </a>
</p>

# servo-bot
A Twitch/Discord bot for Magic: The Gathering streams

## Table of Contents
* [About the Project](#about-the-project)
* [Bugs And Questions](#bugs-and-questions)
* [Deployment](#deployment)

## About the Project

ServoBot is a Twitch bot built for channels that stream Magic: The Gathering. Its features include

* Twitch and Discord integration
* Custom commands
* Automatic chat messages
* Discord queue for games
* Special features available upon request

## Bugs and Questions

Questions about ServoBot or to bugs can be reported in the ServoBot Discord server

[![Discord Server](https://discord.com/api/guilds/678485397176582146/embed.png?style=banner2)](https://discord.gg/w7uJW67)
                                                     
## Deployment

ServoBot is built as a webserver that connects with the Twitch and Discord APIs to be able to monitor chats.
The service must be backed by a MySQL database. This server can be run on an online hosting platform such as Amazon AWS.

These are the detailed instructions on how to deploy it to AWS.

### Reserve an amazon EC2 instance  

For the most part follow the steps in this [guide](https://www.guru99.com/creating-amazon-ec2-instance.html)

1. For the AMI, select `Amazon Linux 2 AMI`
1. The instance type can be a micro, such as t2.micro for the free tier.
  The MoosersBot instance is using t3a.micro because at the time of reserving it was a tad cheaper.
1. Instance details should be as follows
    * Number of Instances: 1
    * Request Spot Instances: Check (This can be no, but spot instances tend to be cheaper though may get taken down
    during high load times.)
    * Credit specification, unlimited: Uncheck (I'm not sure if this will come up, but unchecking to make sure an
    errant server won't spend all my money)
1. Add a tag with the key `Name` and value to reflect the name of the instance.
1. For the security group configuration
    * Rename the security group as you see fit
    * There should be an SSH port for logging into the instance remotely.
    I typically set it for `My IP` to limit who can connect remotely.
    * There needs to be a port for serving the web pages to everyone.
    The server is currently configured to serve on port 5000. 
1. Launching the instance will create a Key needed to access it.
   Download the private key (.pem file) and copy it into the `secret` directory.

### 🚧 *TODO: Configure the database * 🚧

1. A MySQL database needs to be set up with the schema in deploy\create_database.sql and initialized with
    the secret keys from the Twitch and Discord client applications. Currently, it is set up as an RDS database.
1. In the RDS security group, add an inbound rule with a MySQL/Aurora rule from the source security group created
    for the EC2 instance.

### Configure the instance
    
To connect to the instance, follow the instructions in this [guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html)

1. Update dependencies: `sudo yum update`
1. Install java: `sudo yum install java-1.8.0-openjdk`
1. Create directories
    ```sh
        sudo mkdir /var/servo-bot
    ```
1. Enable the ServoBot service 🚧 *Still working on this step* 🚧
    1. Upload the service file and jar to the instance's home directory
        ```sh
            scp -i secret\<pem-filename> src\deploy\servobot.service ec2-user@<instance-hostname>:~
            scp -i secret\MoosersBotEC2.pem build\libs\servo-bot-1.0-SNAPSHOT.jar ec2-user@<instance-hostname>:~
        ```
    1. Copy the service file to the right location
        ```sh
            sudo cp ~/servobot.service /etc/systemd/system
            sudo cp ~/servo-bot-1.0-SNAPSHOT.jar /bin
        ```
    1. start the service
        ```sh
            sudo systemctl enable servobot
            sudo systemctl start servobot
        ```

🚧 *TODO: Figure out logging * 🚧
🚧 *TODO: Figure out HTTPS * 🚧
🚧 *TODO: Document setting up DNS entry * 🚧
    
### Deploy a new version

 🚧 *TODO: update this step* 🚧
