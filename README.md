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

- The application provides direct links
into your Fanshawe Online and FOL-Email.
- Your username and password are always encrypted and they are 
never given to the device or browser.

##  Security

### ObfuscatedSharedPreferences

  Wraps any ````SharedPreference```` object and transperently encrypts all preference values
  but **NOT the keys**.
  
  Values are encrypted using both a constant key which must be changed beore building and
  by a generated device apecific key. If at anypoint either of the keys are changed either though
  an application update or by a significant device change the preferences will self destruct and
  all data will be lost
  
### FOL Single-Sign-On (SSO) urls and NTLM

  The [myFanshawe Portal](https://portal.myfanshawe.ca) which in un-utilized by many students provides 
  SSO urls for FanshaweOnline (FOL) and many other college services. Authentication to myFanshawe is
  done by NTLM over SSL, FasnshaweConnect takes adventage of this setup by enforcing that communiation
  is always done over SSL and the users credentials are never transmited in plain text because of the
  challage-response base nature of NTLM. Further more this allows FanshweCoonect to give SSO urls to the 
  android intent system so the user can access FOL and their email without ever sending credentials
  to another application.

##  Building

Fanshwe Connect uses the new Android-Gradle build system :

- Start by cloning the needed repositories :
    
 ```sh
git clone https://github.com/GzFighter/fanshawe-connect.git FanshaweConnectProject
cd FanshaweConnectProject
git submodule update --init libraries/ntlm/
```

- Modify some files :

 + open ca.GabrielCastro.fanshaweconnect.util.SecretKeyGenerator.java
 + modify the `getSecretKey` method by changing the randomly generated Base64 String

- Build using the included gradle wrapper
 ```sh
./gradlew build
```
