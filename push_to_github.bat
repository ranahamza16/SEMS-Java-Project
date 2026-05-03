@echo off
echo Configuring Git identity...
git config --global user.name "Hamza Rana"
git config --global user.email "ranahamza16@github.com"

echo Initializing Git repository...
git init
git add .
git commit -m "Final project release with UI and Notification fixes"
git branch -M main
git remote add origin https://github.com/ranahamza16/SEMS-Java-Project.git

echo =======================================================
echo Pushing to GitHub... 
echo A WINDOW WILL POP UP ASKING YOU TO SIGN IN TO GITHUB!
echo Please click "Sign in with browser" and authorize it.
echo =======================================================
git push -u origin main
pause
