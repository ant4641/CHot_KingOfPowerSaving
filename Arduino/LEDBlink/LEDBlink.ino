#include <ESP8266WiFi.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
const char* ssid     = "Hao";
const char* password = "850705mm";
const char* host = "172.20.10.7";
void setup() {
  Serial.begin(115200);
  delay(10);
  WiFi.begin(ssid, password);   // We start by connecting to a WiFi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
 // pinMode(D10, OUTPUT); 
  Serial.println("Booting");
  //WiFi.mode(WIFI_STA);
  while (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("Connection Failed! Rebooting...");
    delay(5000);
    ESP.restart();
  }
  ArduinoOTA.onStart([]() {
    Serial.println("Start");
  });
  ArduinoOTA.onEnd([]() {
    Serial.println("\nEnd");
  });
  ArduinoOTA.onProgress([](unsigned int progress, unsigned int total) {
    Serial.printf("Progress: %u%%\r", (progress / (total / 100)));
  });
  ArduinoOTA.onError([](ota_error_t error) {
    Serial.printf("Error[%u]: ", error);
    if (error == OTA_AUTH_ERROR) Serial.println("Auth Failed");
    else if (error == OTA_BEGIN_ERROR) Serial.println("Begin Failed");
    else if (error == OTA_CONNECT_ERROR) Serial.println("Connect Failed");
    else if (error == OTA_RECEIVE_ERROR) Serial.println("Receive Failed");
    else if (error == OTA_END_ERROR) Serial.println("End Failed");
  });
  ArduinoOTA.begin();
  Serial.println("Ready");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

int value = 0;
void loop() {
  delay(5000);
  ++value;
  WiFiClient client;
  // Use WiFiClient class to create TCP connections
  const int httpPort = 8000;
  Serial.print("port:"+httpPort);
  if (!client.connect(host,httpPort)) {
    Serial.println("connection failed");
    return;
  }
  // This will send the request to the server
  client.println("Hi,Server");
  // Read all the lines of the reply from server and print them to Serial
  String line;
  while(client.available()){
    line = client.readStringUntil('\r');
    if(line.equals("1")){
      for(int i=0;i<15;i++){
        ArduinoOTA.handle();
        digitalWrite(D10, HIGH);   
        delay(500);                 
        digitalWrite(D10, LOW);
        delay(500);
      }
    }
  }
   unsigned long timeout = millis(); 
  while (client.available() == 0) {
    if (millis() - timeout > 5000) {
      Serial.println(">>> Client Timeout !");
      client.stop();
      return;
    }
  }
  Serial.println();
  Serial.println("closing connection");
}

