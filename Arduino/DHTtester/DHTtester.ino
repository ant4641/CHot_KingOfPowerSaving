#include "DHT.h"
#include <ESP8266WiFi.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#define DHTPIN 4 // D4 = GPIO 2
#define DHTTYPE DHT11   // DHT 11
const char* ssid     = "MoodleBox"; //The name of the internet you want to access
const char* password = "rpi708708"; //The password of the internet
const char* host = "10.0.0.166"; //Server's ip (shuld connect to the same internet)
DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  delay(10);
  WiFi.begin(ssid, password);   // We start by connecting to a WiFi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  dht.begin();
}

void loop() {
   delay(5000);
  bool con_sta = true;
  WiFiClient client;//宣告客戶端
  const int httpPort = 2000;
  if (!client.connect(host,httpPort)) {
    Serial.println("connection failed");
    delay(5000);
    return;
  }
  else{
    client.println("A3/wet");
    Serial.println("pass succeed");
    int h = dht.readHumidity();
    int t = dht.readTemperature();  
    while(con_sta==true){
      while(client.available()){
        char line = client.read();
        Serial.println(line);
        ArduinoOTA.handle();
        switch(line){
         case '1':
            if (isnan(h) || isnan(t)) {
              Serial.println("Failed to read from DHT sensor!");
              return;
            }
            Serial.print("Humidity: ");
            Serial.print(h);
            Serial.print(" %\t");
            Serial.print("Temperature: ");
            Serial.print(t);
            Serial.print(" *C\n ");
            delay(3000);
            client.println(t);
            break;
            
        case '2':
          client.println("yes");
          break;
    }   
   }
    if( WiFi.status() != WL_CONNECTED){
      con_sta = false;
    }
  }
}
}

