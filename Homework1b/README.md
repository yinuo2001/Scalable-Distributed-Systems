# Homework 1b

## 1. Cross-compile .go file

`
GOOS=linux GOARCH=amd64 go build -o main main.go
`


## 2. Set up EC2 instance

Security group setting:

1. ssh: port 22 (myIP)
2. Custom TCP rules: port 8080 (myIP + neuvan IPs)

ssh into EC2 instance:
`
ssh -i <my-amazon.pem> ec2-user@<instance-public-ipv4-address>
`

Intruction to install Tomcat:
https://techviewleo.com/install-tomcat-on-amazon-linux/

## 3. Upload binary executable file to EC2

Run following command to allow permission to upload files
`
(sudo) chmod -R 777 <dir_name>
`

Run following command to copy local file to EC2 instance
`
sudo scp -i <path/to/pem/file/go.pem> <path/to/pasted/file> ec2-user@<EC2-public-ipv4-address>:<target folder>
`
