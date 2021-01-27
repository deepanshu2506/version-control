# version-control

this project is a shot at trying to mimic the operations performed by git and to improve them and add new features to the version control system in the future

## Supported Commands
**add to staging area**
this command works similar to the git add command.
	

    vcs add [filename | directory-name | .]
	   
**Create a commit**
this command can be used to create a snapshot of the files that are added to the staging area by the user.

    vcs commit "commit message"
	    
**view status of files**
this command gives a detailed listing of the files that have beem modified, staged and deleted since the last commit
	

    vcs status
	    
**view the history of commits**
this command is used to view the details of the previous commits in the branch
	

    vcs log
    
** create or switch to branch**
this command creates a new branch if not present or just switches to it if the branch is present.
	
    vcs checkout branch-name

**view branches**
this command lists all the branches and points to the current working branch
	

    vcs branch
	    
	    
## setup

 - clone the repo
 - create this file "D:\\versionControl\\repos.vcs
 - keep the service.bat file running
 - place the vcs.bat file on the system path

and you are good to go


## contributing
following features are to be developed
 - [ ] merging branches
 - [ ] restoring repo state to a past commit
 - [ ] ignoring files similar to gitignore
 - [ ] reducing the size of the .vcs folder
 - [ ] reducing the space utilised by the file index
 
 create an issue with the problem that you are going to solve and then submit a pr after you complete with the solution

## web-portal
A web portal on lines of github for this version-control-system is to be developed
Feel free to knock yourself and come up with a solution.




