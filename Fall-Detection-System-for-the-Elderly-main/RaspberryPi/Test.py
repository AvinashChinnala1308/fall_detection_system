from Panic import Panic
import time
while True:
    try:
        buzzer.on()
        p = Panic()
        status = p.panicStatus()
        print(status)
        time.sleep(1)
        pass
    except:
        print("error")
        pass

