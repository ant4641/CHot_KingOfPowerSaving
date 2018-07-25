#include <ESP8266WiFi.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
const char* ssid     = "MoodleBox"; //The name of the internet you want to access
const char* password = "rpi708708"; //The password of the internet
const char* host = "10.0.0.166"; //Server's ip (shuld connect to the same internet)
int photocellPin = 0; // 光敏電阻 (photocell) 接在 anallog pin 0
int photocellVal = 0; // photocell variabl

void setup() {
  Serial.begin(115200);
  delay(10);
  WiFi.begin(ssid, password);   // We start by connecting to a WiFi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  pinMode(photocellPin, OUTPUT);//設定光度感測器
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
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
    client.println("A2");
    Serial.println("pass succeed");
    while(con_sta==true){
      while(client.available()){
        char line = client.read();
        Serial.println(line);
        ArduinoOTA.handle();
        switch(line){
         case '1':
            photocellVal = analogRead(photocellPin);  
            photocellVal = 1024-photocellVal; 
            Serial.println(photocellVal);
            client.println(photocellVal);              
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
