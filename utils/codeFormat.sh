#This script runs executes formatting program for given wildcard files.
#For example calling this script with "src/*.java" argument will format
#all java sources int src directory tree.
#astyle creates backups with unformatted code with added .orig extension
#this script delete those files so we will have clean source directory.
#To use this script you need to install astyle you can do that by writing
#sudo apt-get install astyle

astyle -T4 -o -r $1 
find . -name "*.orig"  -exec rm  {} \;