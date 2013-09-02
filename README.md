#  Fanshawe Connect

##  About

Fanshawe Connect is an Android App designed to ease the use of
android devices at **Fanshawe College of Applied Arts and Technology** 
in London Ontario.

##  Wi-Fi Auto-Connect

- Automatically detects when the device is connected to 
`FanshaweStudents` or `FanshaweEmployees` wireless networks
and authenticats with the captive portal login automatically.
- Will only ever authenticate through a secure HTTPS connection to 
protect the device from phishing attacks.

## FOL Device Authentication

- The application provides direct Single-Sign-On (SSO) links
into Fanshawe Online and FOL-Email.
- Your username and password are always encrypted and they are 
never given to the device or browser.

##  Building

Fanshwe Connect uses the new Android-Gradle build system :

- Start by cloning the needed repositories :
    
 ```sh
git clone https://github.com/GzFighter/fanshawe-connect.git FanshaweConnectProject
cd FanshaweConnectProject
git submodule update --init libraries/ntlm/
```

- Modify some files :
 
 + open ca.GabrielCastro.fanshaweconnect.util.ObfuscateSharedPreferences.java
 + modify the `SEKRIT` variable by changing the randomly generated Base64 String

- Build using the included gradle wrapper
````
./gradlew build
````
