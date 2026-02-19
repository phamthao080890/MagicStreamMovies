# Streaming APIs - Backend Setup Instructions

## Requirements / Installations

Install the following things

- IntelliJ **Community Edition**
    1. [Download IntelliJ](https://www.jetbrains.com/idea/download/?var=1&section=mac)

- Install Java 17 using IntelliJ
  1. Open IntelliJ. Go to `File -> Project Structure`. 
  2. On the left, go to `Platform Settings -> SDKs`. 
  3. Click on the `+` sign at the top of the middle column, select `Download JDK`. 
  4. Select version `corretto-17` as the vendor, then click `Download`. 
  5. Check the version installed `/usr/libexec/java_home -V`
      ```
      $ /usr/libexec/java_home -V
      Matching Java Virtual Machines (1):
          17.0.15 (arm64) "Amazon.com Inc." - "Amazon Corretto 17" /Users/user/Library/Java/JavaVirtualMachines/corretto-17.0.15/Contents/Home
      ```

  6. Set the `JAVA_HOME` and `JAVA_17_HOME` environment variables by adding the following to `~/.bash_profile`.
     If you use a different shell or prefer to set the variable in another file, you can do that instead.
     ```
     export JAVA_17_HOME=$(/usr/libexec/java_home -v17)
     alias j17='export JAVA_HOME=$JAVA_11_HOME'
     ```

  7. Reload the terminal and check that the variables are set.
      ```
      $ source ~/.bash_profile
      $ j17
      $ echo $JAVA_17_HOME
      /Users/user/Library/Java/JavaVirtualMachines/corretto-17.0.15/Contents/Home
      ```

- mongoimport
   ```
   $ brew install mongodb-community@7.0
   $ brew install --cask mongodb-compass
   $ mongoimport --version
   mongoimport version: 100.14.0
   git version: d909d040daa15cc1356071e835e4bac31a96dce3
   Go version: go1.24.0
      os: darwin
      arch: arm64
      compiler: gc
   $ brew services start mongodb/brew/mongodb-community@7.0
   ```

## Setup
Note: Make sure Java and mongodb versions are correct and mongodb is started.

1. Import the Database using either the `mongoimport` client
   ```
   $ cd <path to json-files>
   $ mongoimport --db mystreamingdb --collection movies --file movies.json –jsonArray 
   $ mongoimport --db mystreamingdb --collection rankings --file rankings.json --jsonArray
   $ mongoimport --db mystreamingdb --collection genres --file genres.json --jsonArray
   $ mongoimport --db mystreamingdb --collection users --file users.json --jsonArray
   ```
   
2. Configure IntelliJ
    - Open the cloned `backend` folder in IntelliJ
    - For code navigation to work, right-click on the `pom.xml` file and select "Add as Maven Project"
    - Make sure IntelliJ points to Java 17
      `File -> Project Structure`. Set the `Project SDK` setting to 17
    - Setup [Run configurations](#intellij-run-configurations) in IntelliJ.

## Running
The Spring Boot application can be run from within IntelliJ.
   ```
    $ mvn clean install
    $ mvn spring-boot:run
   ```

Or run the application using the run configurations set up in IntelliJ.

### IntelliJ Run Configurations

#### Local

This configuration is saved in `.idea/runConfigurations/Local.xml` and should be available when IntelliJ is started.
If required, additional run configurations can be added by following the steps and sample configurations below.

#### Other Run Configurations

On the top right side of IntelliJ, between the build project and run project icons, there is a dropdown with the selected run configuration
Click on the dropdown and select `Edit Configurations...`. A dialog box will pop up.

Click on the `+` sign on the top left of the dialog box to add a new configuration.
