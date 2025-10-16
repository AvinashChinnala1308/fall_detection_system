import requests
import json
base = 'https://falldetection-74ff4-default-rtdb.firebaseio.com/'
class Rest:
    
    def post(self,url,data):
        resturl = base+url+'.json'
        print(resturl)
        r = requests.post(resturl, data)
        return json.loads(r.text)
    
    def get(self,url):
        resturl = base+url+'.json'
        r = requests.get(resturl)
        return json.loads(r.text)
    
    
    def delete(self,url):
        resturl = base+url+'.json'
        r = requests.delete(resturl)
        return json.loads(r.text)
    
    
    def put(self,url,data):
        resturl = base+url+'.json'
        r = requests.put(resturl, data)
        return json.loads(r.text)

    def load(self,url):
        resturl = base+url+'.json'
        r = requests.get(resturl)
        return json.loads(r.text)
    def sendNotification(self, title, body, resturl):
        headers = {"content-type": "application/json",
                   "Authorization":"key=AAAA7_CK-m0:APA91bGBRBZKPvhLBl5Pb6Re5cgYmJumpEnHUnihd4NG1X2tC32bwu1PwPmcTSrw6---cAowVvzhje2b3q6adO0t5jpdveZww7FfUp6hfxd2xj1kBoyAd114z_mPNfhN6xEO-kx9LkT0"}
        url = base+resturl+".json"
        print(url)
        r = requests.get(url)
        data = json.loads(r.text)
        note = {
             "to":data['token'],
             "content_available":True,
             "notification": {
                "title": title,
                "body":body,
                "click_action":"fcm.ACTION.HELLO"
             }
            }
        r = requests.post("https://fcm.googleapis.com/fcm/send", data=json.dumps(note), headers=headers)
        return r.status_code, r.reason
        