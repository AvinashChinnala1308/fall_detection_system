import RPi.GPIO as G
class Panic:
    
    def panicStatus(self):
        G.setwarnings(False)
        G.setmode(G.BOARD)
        G.setup(36,G.OUT)
        G.setup(38,G.OUT)
        G.setup(40,G.IN)
        G.output(36,G.HIGH)
        G.output(38,G.LOW)
        return G.input(40)
