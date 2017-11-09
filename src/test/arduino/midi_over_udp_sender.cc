#include <ESP8266WiFi.h>
#include <WiFiUdp.h>


const char* ssid = "*******";
const char* password = "*******";

WiFiUDP Udp;


void setup()
{
  Serial.begin(115200);
  Serial.println();

  Serial.printf("Connecting to %s ", ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)  {
    delay(500);
    Serial.print(".");
  }
  Serial.println(" connected");
  Serial.printf("Connected as  %s\n", WiFi.localIP().toString().c_str());
  pinMode(LED_BUILTIN, OUTPUT);     // Initialize the LED_BUILTIN pin as an output
}


// This just happened to be my address when writing this code,
// obviously it should be something else on your network.
const IPAddress  ip(10, 0, 0, 62);

void loop()
{
  // Switch the LED off
  digitalWrite(LED_BUILTIN, LOW);
  //                    Chan cmd   note  velocity
  byte keyOnBytes[] =  {0,   0x90, 0x44, 0x45};
  byte keyOffBytes[] = {0,   0x80, 0x44, 0x45};

  // Send a key down event
  Udp.beginPacket(ip, 6565);
  Udp.write(keyOnBytes, 4);
  Udp.endPacket();
  Serial.println(" key down");

  // Turn the LED off by making the voltage HIGH
  digitalWrite(LED_BUILTIN, HIGH);

  // Wait half a  second
  delay(500);

  // Send a key up event
  Udp.beginPacket(ip, 6565);
  Udp.write(keyOffBytes, 4);
  Udp.endPacket();
  Serial.println(" key up");
  digitalWrite(LED_BUILTIN, LOW);

  // Wait three seconds, and do it all again.
  delay(3000);
}
