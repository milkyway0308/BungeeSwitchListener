## BungeeSwitchListener
매우 간단했던 번지코드 패킷 브로드캐스터

### 이게 뭐죠?
번지코드 기반 서버에서 플레이어 데이터를 사용할 때, 저장과 로드의 타이밍 이벤트로 발생시켜줍니다.

### 어떻게 작동하죠?
플레이어가 서버를 옮기는 순간, 번지코드 플러그인(`BungeeSwitchListener`)에서 이전 서버에 저장 요청 이벤트(PlayerSaveRequestEvent)를 호출시켜줍니다.
이벤트가 지나간 후, 플레이어가 옮겨간 서버에 로드 요청 이벤트(`PlayerLoadRequestEvent`)를 최소 50ms 뒤에 발생시켜줍니다. 

### 어떻게 쓰죠?
1. BungeeSwitchListener은 번지코드에, BukkitSwitchHandler은 사용할 서버들에 설치하세요.
2. 플러그인에서 EventHandler을 통해 이벤트를 처리합니다.<br>
3-1. 만약 업데이트가 필요한 경우, `BukkitSwitchHandler#reloadRequest(String category)` 메서드를 통해 리퀘스트를 생성하세요.<br>
3-2. `PlayerReloadRequest#write(String|double|float|int|char|byte|byte[])` 메서드로 원하는 데이터를 보내세요.<br>
3-3. `PlayerReloadRequest#send(UUID)` 메서드로 대상 플레이어가 접속한 서버에 데이터 정정 요청을 보내세요.<br>
3-4. 대상 서버에서 `PlayerLoadRequestEvent` 이벤트를 받아 처리하세요.<br>

### 트로잔이라는 이름이 보이는거같은데 뭐죠?
해당 프로젝트의 불러오기(Reload) 리퀘스트는 존재하지 않는 플레이어의 커넥션을 생성해 번지코드에 전송합니다.
패킷이 전달되면 안되기 때문에 중간에 패킷을 가로채야하고, 이 이유로 버킷 내부의 핸들러를 교체합니다.
이 기능으로 인해 트로잔이라는 이름의 기능이있습니다.