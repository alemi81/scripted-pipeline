template = '''
apiVersion: v1
kind: Pod
metadata:
  name: terraform
spec:
  containers:
  - image: ikambarov/terraform:0.14
    name: terraform
    '''

tfvars = '''
environment   = "dev"	
s3_bucket     = "jenkins-terraform-evolvecybertraining"
s3_folder_project = "terraform_ec2"
s3_folder_region = "us-east-1"
s3_folder_type = "class"
s3_tfstate_file = "infrastructure.tfstate"
region        = "us-east-1"	
public_key    = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local"	
ami_id        = "ami-02eac2c0129f6376b"
'''

podTemplate(cloud: 'kubernetes', label: 'terraform', showRawYaml: false, yaml: template) {
    node("terraform"){
        container("terraform"){
            stage("Pull Code"){
                git 'https://github.com/ikambarov/terraform-ec2.git'
            }

            withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                stage("Init"){
                    writeFile file: 'my.tfvars', text: tfvars
                    sh "/bin/sh setenv.sh my.tfvars"
                    sh "terraform init"
                }
                
                stage("Apply"){
                    sh "terraform apply -var-file my.tfvars -auto-approve"
                }
            }
        }
    }
}
