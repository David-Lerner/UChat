# About
# Installation
To run the command line version of UChat, follow the following steps:
1. Download the ZIP source files from Github
2. Unzip the files
3. Open the command line and navigate to within the src folder
   (On Windows one can navigate to the src folder with the file explorer,
   then right click while pressing shift anywhere in the folder window 
   and select "Open command window here" to open the commmand line there)
4. compile the program first by entering   `javac uchat/model/*.java`
   It will warn you that some methods are depreciated. Ignore it.
   Then enter `javac uchat/main/CLIMain.java`
5. run the compiled program with `java uchat/main/CLIMain`