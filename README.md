ChunkHah 플러그인
===

각별이라는 아주위대하고도위대한 플러그인 개발자~이자 유튜버~분이 제작하신
[이웃집 야생](https://www.youtube.com/watch?v=RbS_oqxLjaQ)이란 컨텐츠에 사용된 플러그인이 배포가 안되있어서
직접 만든 플러그인입니다.

자동 할당이 켜져 있을시 플레이어가 최초 접속시에 0, 0 청크 주변으로 나선형으로 돌아가며 할당이 됩니다.
할당이 되어있는 플레이어는 자신이 할당받은 청크 밖으로 나가지 못하며,
청크 밖 영역에 대한 블럭 설치, 파괴, 상호작용 또한 불가능합니다.
(엔티티 상호작용 / 공격은 가능합니다.)
~보트를 통해서 청크 밖으로 나갈수 있습니다~ ~+아이템 액자 주의 액자는 엔티티~

그 외에도, 무언가에 탑승해 있을경우 (마인카트, 말, 스트라이더) 청크 밖으로 나갈수 있습니다.
(내릴시 바로 청크 내부로 tp 하며, ~끼여 죽을수도~)



네더의 스포너
===
플레이어에게 할당된 네더 청크 하나당, 무작위 y 좌표에 블레이즈 스포너, 엔더맨 스포너가 한개씩 생성됩니다.



충돌 처리
===

기본적으로 청크 밖으로 나갈려고 시도할경우, 같은 y 좌표의 청크 안쪽 위치로 tp 한 후,
반대쪽 방향으로 조금 튕겨냅니다.

다만 엔더펄을 사용하는등, 플레이어의 y 좌표가 청크 밖에서 크게 변동되는 경우,
땅에 끼여 죽는 문제가 발생할수 있기에, 이런 경우를 최대한 줄이기 위해,

안쪽으로 tp 할 시, 플레이어가 닿는 모든 블럭에 대해 플레이어를 질식시킬수 있는 블럭이 있는지 확인하고,
만약 있을경우 y 좌표를 1만큼 올려서 확인하길 반복합니다.

다만 끼여 죽는 경우가 완전히 방지되진 않으며, 질식 가능한 블럭의 기준이 완벽하지 않습니다.



명령어
===

영역 제어 명령어
* `savearia` : `plugins\ChunkHah\aria.json` 파일에 현재 영역 정보를 저장합니다.
* `loadaria` : `plugins\ChunkHah\aria.json` 파일에 저장된 영역 정보를 불러옵니다.
* `setchunk <player> <chunk_x> <chunk_z>` : 특정 플레이에의 청크를 지정된 위치로 설정합니다.
* `clearallchunk` : 모든 플레이어의 `ChunkManager` 를 제거하고, 모든 플레이어와 청크의 제약을 해제합니다.
* `rmchunk <player>` : 특정 플레이어의 `ChunkManager` 를 제거하고, 해당 플레이어의 청크 제약을 해제합니다.

제약 제어 명령어
* `bind <player>` : 특정 플레이어에 대한 청크 제약을 활성화 합니다.
* `unbind <player>` : 특정 플레이어에 대한 청크 제약을 비활성화 합니다.

청크 정보 명령어
* `allchunkmanager` : 모든 `ChunkManager`에 대한 정보를 확인합니다.
* `chunkmanagerinfo` : 현재 위치의 `ChunkManager`에 대한 정보를 확인합니다.



TODO
===

버그
===
