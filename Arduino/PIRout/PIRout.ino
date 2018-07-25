#include <ESP8266WiFi.h>
#include <ESP8266mDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
const char* ssid     = "MoodleBox"; //The name of the internet you want to access
const char* password = "rpi708708"; //The password of the internet
const char* host = "10.0.0.166"; //Server's ip (shuld connect to the same internet)
const byte pirPin = 12;  //PIR的輸入port
 

void setup() {
  Serial.begin(115200);
  delay(10);
  WiFi.begin(ssid, password);   // We start by connecting to a WiFi network
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  pinMode(pirPin,INPUT);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
} 

void loop() {
  delay(5000);
  int count = 0;
  bool con_sta= true;
  bool scan_sta = true;
  WiFiClient client;//宣告客戶端
  const int httpPort = 2000;
  if (!client.connect(host,httpPort)) {
    Serial.println("connection failed");
    delay(5000);
    return;
  }
  else{
    client.println("A2/infra");
    while(con_sta==true){
      while(client.available()){
        char line = client.read();
        ArduinoOTA.handle();
        switch(line){
         case '1':
            for(int i=0;i<2;i++){
              int val=digitalRead(pirPin);
              if (val== 1) {   //PIR 有偵測到時 : LED 閃一下
                 scan_sta = true;
                 count=0;
                 delay(1000); // wait for a second
              }
              else{  //PIR 沒有偵測到 : LED 暗
                 count++;
                 if(count == 2 ){
                    scan_sta = false;
                 }
                 delay(1000);// wait for a second
              }
            }
              if(scan_sta == true){
                client.println("0");
              }
              else{
              client.println("1");  
              break;
            }              
            

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

