# Ontology Processing Script

## Overview

This shell script automates the process of running an ontology processing program using Maven. It provides a convenient way to execute a Java application with specific ontology file and class name arguments.

## Prerequisites

Before using this script, ensure you have the following installed:

### 1. Java Development Kit (JDK)
- **Windows**: Download from [Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Linux**: Use package manager
  ```bash
  sudo apt-get install openjdk-11-jdk  # Ubuntu/Debian
  sudo yum install java-11-openjdk     # CentOS/RHEL
  ```
- **macOS**:
  ```bash
  brew install openjdk@11
  ```

### 2. Maven
- **Windows**:
    1. Download from [Apache Maven](https://maven.apache.org/download.cgi)
    2. Add Maven to system PATH

- **Linux**:
  ```bash
  sudo apt-get install maven   # Ubuntu/Debian
  sudo yum install maven       # CentOS/RHEL
  ```

- **macOS**:
  ```bash
  brew install maven
  ```

## Script Location and Structure

- Place the script in your project's root directory
- Create an `ontologies` subdirectory to store ontology files

Project structure should look like:
```
project-root/
│
├── ontologies/
│   ├── ecso.the-ecosystem-ontology.50.owl.xml
│   └── other-ontology-files...
│
├── pom.xml
├── assignment_2_kr (script)
└── src/
    ├── main.java
    └── ELReasoner.java
```

## Usage

### Example Command

```bash
./assignment_2_kr ecso.the-ecosystem-ontology.50.owl.xml PATO_0005020
```

### Detailed Usage

- First argument: Ontology filename (must exist in `ontologies/` directory)
- Second argument: Target class name for processing

### Linux and macOS

1. Make the script executable:
   ```bash
   chmod +x assignment_2_kr
   ```

2. Run the script with two arguments:
   ```bash
   ./assignment_2_kr ecso.the-ecosystem-ontology.50.owl.xml PATO_0005020
   ```

### Windows

1. Use Git Bash, Windows Subsystem for Linux (WSL), or create a equivalent batch script

   Batch script equivalent (`assignment_2_kr.bat`):
   ```batch
   @echo off
   if "%~2"=="" (
       echo Usage: assignment_2_kr.bat ONTOLOGY_FILE CLASS_NAME
       exit /b 1
   )

   set ONTOLOGY_PATH=%~dp0\ontologies
   set ONTOLOGY_FILE=%ONTOLOGY_PATH%\%1
   set CLASS_NAME=%2

   mvn exec:java -Dexec.mainClass="org.vu.kr.Main" -Dexec.args="%ONTOLOGY_FILE% %CLASS_NAME%"
   ```

2. Run in Command Prompt or PowerShell:
   ```powershell
   .\assignment_2_kr.bat ecso.the-ecosystem-ontology.50.owl.xml PATO_0005020
   ```

## Troubleshooting

1. **Maven not found**:
    - Verify Maven is installed
    - Check system PATH configuration

2. **Java not recognized**:
    - Confirm Java is installed
    - Set JAVA_HOME environment variable

3. **Permission issues (Linux/macOS)**:
    - Use `sudo chmod +x assignment_2_kr` if execution fails

## Additional Notes

- Ensure your `pom.xml` is configured to run the specified main class
- Ontology files must be pre-downloaded and placed in the `ontologies/` directory