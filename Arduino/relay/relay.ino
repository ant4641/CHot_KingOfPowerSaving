
int control = 4;
// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin LED_BUILTIN as an output.
  pinMode(control, OUTPUT);
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(control, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(20000);                       // wait for a second
  digitalWrite(control, LOW);    // turn the LED off by making the voltage LOW
  delay(10000);                       // wait for a second
}
