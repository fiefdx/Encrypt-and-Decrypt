Encrypt & Decrypt
=================
A java application for file encrypt & decrypt, use TEA as encrypt & decrypt algorithm

ScreenShots
-----------
1. Working like this
   
   ![Alt text](/doc/main_window.png?raw=true "main_window")

   ![Alt text](/doc/encrypt_file.png?raw=true "encrypt_file")

   ![Alt text](/doc/parse_file_name.png?raw=true "parse_file_name")

   ![Alt text](/doc/decrypt_file.png?raw=true "decrypt_file")

Build & Run
-----------
1. Build it
   ```
   git clone git@github.com:fiefdx/Encrypt-and-Decrypt.git Encrypt-and-Decrypt
   Use eclipse import Encrypt-and-Decrypt as existing java project
   Export Java Runnable JAR file(ex: Crypt.jar)
   ```
2. Run it
   
   ```bash
   # Create a directory
   mkdir Encrypt-and-Decrypt
   # Copy Crypt.jar to Encrypt-and-Decrypt directory
   cp Crypt.jar Encrypt-and-Decrypt/Crypt.jar
   # Copy img directory to Encrypt-and-Decrypt directory
   cp -r img Encrypt-and-Decrypt
   # run it
   java -jar Crypt.jar
   ```
3. Encrypt file

   Select a file, Click Encrypt, Input password, Click OK
4. Parse encrypted file's original file name

   Select an encrypted file, Click Parse, Input password, Click OK
5. Decrypt file
   
   Select an encrypted file, Click Decrypt, Input password, Click OK
   
