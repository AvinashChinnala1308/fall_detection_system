from Rest import Rest
from MyAdxl import MyAdxl
from Buzzer import Buzzer
from Panic import Panic
import time
try:
    kitId ='15'
    url = 'FallDetection'
    rest = Rest()
    adxl = MyAdxl()
    buzzer = Buzzer()
    panicc = Panic()
    result = rest.load(url+'/coordinates/'+kitId)
    xp = float(result['xp'])
    xn = float(result['xn'])
    yp = float(result['yp'])
    yn = float(result['yn'])
    zp = float(result['zp'])
    zn = float(result['zn'])
    data = '{"id":"'+kitId+'","status": "Safe"}'
    result = rest.put(url+'/status/'+kitId,data)
    buzzer.off()
    def myloop():
        while True:
            try:
                adxlResult = adxl.getAxis()
                print(adxlResult)
                if xn < adxlResult['x'] < xp and yn< adxlResult['y'] < yp and zn < adxlResult['x'] < zp:
                    pass
                else:
                    danger()
            except Exception as e2:
                print(e2)
            time.sleep(1)
    def danger():
        count = 1;
        while True:
            if panicc.panicStatus()==0:
                if count<=10:
                    buzzer.on()
                    time.sleep(0.5)
                    buzzer.off()
                    time.sleep(0.5)
                else:
                    time.sleep(1)
                if count==10:
                    print('danger')
                    buzzer.on()
                    rest.sendNotification('Fall Detected','Emergency Ewitchvation',url+'/tokens/'+kitId)
                    data = '{"id":"'+kitId+'","status": "Danger"}'
                    result = rest.put(url+'/status/'+kitId,data)
                count =count+1
            else:
                if count!=1:
                    data = '{"id":"'+kitId+'","status": "Safe"}'
                    result = rest.put(url+'/status/'+kitId,data)
                    buzzer.off()
                    break
            
    myloop()
        
        
except Exception as e:
    print(e)
    

    