# meshtoheightmap
Take a mesh and create a heightmap!
Attention: You have to Install Java:

If you encounter the error message "A JNI error has occurred, please check your installation and try again" when running the JAR file on another PC, it indicates that Java might not be installed or configured properly on that machine. Follow these steps to resolve the issue:
1. Download and Install Java

    Download the latest version of the Java Development Kit (JDK) from the official Oracle website or another trusted source:
        Oracle JDK Download
        OpenJDK
    Install the JDK by following the instructions provided on the download page.

2. Configure Environment Variables

After installing the JDK, configure the JAVA_HOME and PATH environment variables.

For Windows:

    Open the Control Panel and go to System and Security > System > Advanced system settings.
    Click on Environment Variables.
    Under System Variables, click New to create a new variable named JAVA_HOME. Set its value to the JDK installation directory (e.g., C:\Program Files\Java\jdk-11.0.11).
    Find the PATH variable under System Variables, select it, and click Edit. Add %JAVA_HOME%\bin at the end of the PATH variable.
    Save the changes and restart the system.

For macOS and Linux:

    Open a terminal.
    Edit the .bashrc, .bash_profile, or .zshrc file, depending on your shell.
    Add the following lines:

    sh

    export JAVA_HOME=/path/to/jdk
    export PATH=$JAVA_HOME/bin:$PATH

    Replace /path/to/jdk with the actual path to the JDK installation directory.
    Save the file and run source ~/.bashrc or source ~/.bash_profile (depending on the file you edited) to apply the changes.

3. Verify the Installation

    Open a command line (CMD on Windows, Terminal on macOS/Linux).
    Enter java -version to check if the correct Java version is displayed.
    Enter javac -version to ensure the Java compiler is also available.

4. Run the JAR File

    Once Java is installed and the environment variables are configured, run the JAR file with the following command:

    sh

    java -jar path/to/jar/yourfile.jar

These steps should resolve the JNI error, and your JAR file should run successfully on the target machine.
