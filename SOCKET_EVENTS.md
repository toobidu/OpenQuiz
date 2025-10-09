# Socket.IO Events Standardization

## Room Events
- `join-room` - Join room
- `leave-room` - Leave room  
- `player-joined` - Player joined room
- `player-left` - Player left room
- `room-players` - Updated players list
- `player-kicked` - Player was kicked
- `host-changed` - Host transferred

## Game Events  
- `start-game` - Start game (host only)
- `game-started` - Game started broadcast
- `next-question` - Next question data
- `submit-answer` - Submit answer
- `answer-submitted` - Answer result
- `game-ended` - Game finished

## Response Events
- `join-room-success` / `join-room-error`
- `leave-room-success` / `leave-room-error`  
- `start-game-success` / `start-game-error`
- `submit-answer-error`
- `next-question-error`