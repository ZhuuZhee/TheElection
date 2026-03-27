# Network ที่ทำได้ตอนนี่

## ฝั่ง Client
## class
- GameClientManager: เชื่อมต่อและจัดการ UI
เวลาผู้เล่นกดปุ่มอะไรในเกม (กดเข้าห้อง, กดจบเทิร์น, กดใช้การ์ด) โปรแกรมจะส่งข้อมูลแบบ JSON แบบ type ไปหา Server 

`(type: "JOIN")`
GameClientManager.connect(ip, port, name)
```json
{
  "type": "JOIN",
  "playerName": "Pong"
}
```

`(type: "END_TURN")`
ต้องมีปุ่มกดจบเทิร์น
```json
{
  "type": "END_TURN"
}
```

`(type: "USE_CARD")`
ลากการ์ดใส่เมืองต่างๆ 
```json
{
  "type": "USE_CARD"
}
```

---

## ฝั่ง Server
## class
- GameServerManager: คุมกฎ ตัดสินใจทั้งหมด และคอยส่งข้อมูลบอกทุก Client
- GameState: เก็บสถานะข้อมูลทั้งเกม
- ClientHandler: เป็นตัวแทนของผู้เล่นบน Server

Player 1 คนสร้างห้องเปืด Server ให้ Client อื่นๆเข้ามา

`(type: "JOIN_ACK")`
Client ขอ Join 
```json
{
  "type": "JOIN_ACK",
  "assignedId": "9b1deb...UUID" 
}
```
`(type: "SYNC_STATE")`
การ Broadcast บอกทุก Client ให้มีข้อมูลเหมือนกัน มีคนใหม่เข้าห้องมา, มีคนจบเทิร์น, มีคนใช้การ์ด จะทำ Sync ทุกรอบหลังผู้เล่นจบเทิร์น
```json
{
  "type": "SYNC_STATE",
  "phaseCounter": 1
  "hostId": "ๆไกๆหฟกหฟก",
  "currentPlayerId": "ฟหกๆไกๆ",
  "players": [
    {
       "playerId": "9b1deb...UUID",
       "playerName": "Pong",
       "coin": 100,
       "cityOwn": ["KUY", "PONG"],
       "actionCards": [],
       "policyCards": []
    },
    และคนอื่นๆ ในห้อง
  ]
}
``` 
