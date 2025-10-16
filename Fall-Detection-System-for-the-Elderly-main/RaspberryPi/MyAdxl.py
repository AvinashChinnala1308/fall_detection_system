from adxl345 import ADXL345
import time
class MyAdxl:
    def getAxis(self):
        adxl345 = ADXL345()
        axes = adxl345.get_axes()
        return axes
        
