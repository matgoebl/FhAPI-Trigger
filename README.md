# FhAPI-Trigger
This app sends and receives data to/from an [FhAPI](https://github.com/matgoebl/FhAPI)
[FHEM](https://fhem.de/) web API endpoint.

## Setup

Set your FhAPI base URL in res/values/strings.xml:

      <string name="baseUrl">https://user:pass@server:12345/fhapi/MyTriggerApp/</string>

Compile and deploy the app to your android device.

Set up FHEM. Here is a sample config:

      define webapi FhAPI fhapi
      attr webapi MyTriggerApp_RWDevices MyTriggerApp

      define MyTriggerApp dummy
      attr MyTriggerApp webCmd test:foo:bar

      define MyTriggerApp_test_Notify notify MyTriggerApp:test "logger MyTriggerApp"

      setstate MyTriggerApp someState123
      setreading MyTriggerApp someReading 456
      setreading MyTriggerApp _appCmd test:foo:bar


## Features

- The app initially fetches and displays the FHEM device state and all readings (using `GET <baseUrl>`).
- Readings beginning with `_` are skipped (they are hidden).
- The special reading `_appCmd` is treated like webCmd for the FHEM web interface: It is split up at `:` into commands that are added as separate buttons.
- Pressing the *Update/Send* button updates all readings.
- Entering a string in the field *Trigger Name* and pressing the *Update/Send* button
  sends it as `GET <baseUrl>?set=<string>`.

![Screenshot1](Screenshot1.png?raw=true "Initial readings") 
![Screenshot2](Screenshot2.png?raw=true "After sending a Trigger")



## TODOs

- Add preferences menu to set base URL
