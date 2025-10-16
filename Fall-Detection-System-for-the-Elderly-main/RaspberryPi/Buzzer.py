import RPi.GPIO as G
class Buzzer:
    G.setwarnings(False)
    G.setmode(G.BOARD)
    def on(self):
        G.setup(8,G.OUT)
        G.output(8,G.HIGH)
    def off(self):
        G.setup(8,G.OUT)
        G.output(8,G.LOW)


