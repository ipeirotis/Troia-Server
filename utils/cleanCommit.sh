#This is scripts for commiting changes to java projects.
#First it formats code, then adds all changes that were made
#to commit and commits them. Finally it rebases and pushes changes 
#to server. Using this script will asure that you commits will always
#be good looking (formated and rebased).
#You use this as normal commit, for example : codeFormat.sh "Commit description"


if [ -n "$1"];
then
echo You must enter commit message as parameter
else
codeFormat.sh "src/*.java"
git add .
git add -u .
git commit -m "$1"
git pull --rebase
git push
fi