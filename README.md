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
    
**configuration**
this command configures the username and email befor using the VCS
	

    vcs configure [username|email] "value"
    
**rollback to previous commit**
this command is useful to rollback to the previous nth commit
	

    vcs rollback n
    nth commit = CURRENT_COMMIT-n
	    
	    
## setup

 - clone the repo
 - create this file "D:\\versionControl\\repos.vcs
 - keep the service.bat file running
 - place the vcs.bat file on the system path

and you are good to go





