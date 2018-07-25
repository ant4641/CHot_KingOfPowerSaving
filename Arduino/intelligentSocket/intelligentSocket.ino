#include <ESP8266WiFi.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
const char* ssid     = "MoodleBox"; //The name of the internet you want to access
const char* password = "rpi708708"; //The password of the internet
const char* host = "10.0.0.166"; //Server's ip (shuld connect to the same internet)
int control = 4; // 繼電器port

void setup() {
  pinMode(control, OUTPUT);//設定繼電器
  digitalWrite(control, HIGH);
  Serial.begin(115200);
  delay(10);
  WiFi.begin(ssid, password);   // We start by connecting to a WiFi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}


void loop() {
  // Read all the lines of the reply from server and print them to Serial
  
  bool con_sta = true;
  WiFiClient client;//宣告客戶端
  const int httpPort = 2000;
  if (!client.connect(host,httpPort)) {
    Serial.println("connection failed");
    delay(5000);
    return;
  }
  else{
    delay(1000);
    client.println("S1");
    Serial.println("pass succeed");
    while(con_sta==true){
      while(client.available()){
        char line = client.read();
        Serial.println(line);
        ArduinoOTA.handle();
        switch(line){
          case '0':
            digitalWrite(control, LOW);
            client.println("/over");
            break;
            
          case '2':
            client.println("yes");
            break;
    }   
   }
   Serial.println("1");
    if(WiFi.status() != WL_CONNECTED){
      con_sta = false;
    }
  }
}
}



