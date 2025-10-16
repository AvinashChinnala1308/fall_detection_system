import requests
import json
class Notification:
    
    

    def sendNotification(self, title, body):
        headers = {"content-type": "application/json",
                   "Authorization":"key=AAAAzjex4-g:APA91bFXepmCKEMRzPv0QSpfsSdtXck6PRia7rHNloVM87IvPRZm-JNrH5_Lm_IOQqpe6_y4_ya_dv34VRapK1XJSkP0UGtGlz9CPcrsxWy7kpDR3QhL6PA0Mm-gIs4mO1_kHTVoCgAd"}
        r = requests.get("https://iotcloud-ef028.firebaseio.com/tokens/RMPG/tokens.json")
        tokens = json.loads(r.text)
        for token in tokens:
            note = {
             "to":token,
             "content_available":True,
             "notification": {
                "title": title,
                "body":body,
                "click_action":"fcm.ACTION.HELLO"
             }
            }
            r = requests.post("https://fcm.googleapis.com/fcm/send", data=json.dumps(note), headers=headers)
            print(r.status_code, r.reason)


    