echo $USER
echo ' -> running'
cp /home/ec2-user/install/servo-bot-1.0-SNAPSHOT.jar /bin
echo ' -> moved'
sudo systemctl restart servobot
echo ' -> restarted'