# QuickCheck
<b>Quickly check a file's checksum to verify if the file is valid or not!</b>

# Usage
Using it is very easy! You only need a file to check with and a console to run this program.

How to install (in Windows):

1. Place the executable on one of the directories or create a new one on the %PATH% environment variable.
2. Open a console, type `quickcheck` to verify if you've done Step 1 correctly. QuickCheck will throw an exception due to having no files passed in.
3. Profit! Pass any file into quickcheck and it will output the checksums of the files. (SHA1, SHA256, MD5)

# Building
Start by cloning this repository (`git clone`) or downloading it. Once done, you can build this using Gradle by `gradlew build`. 
While building, it should also make an executable named `quickcheck.exe`, if you have Launch4j installed.
