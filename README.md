ChunkHah 플러그인
===
각별이라는 아주위대하고도위대한 플러그인 개발자~~이자 유튜버~~분이 제작하신 
[이웃집 야생](https://www.youtube.com/watch?v=RbS_oqxLjaQ)이란 컨텐츠에 사용된 플러그인이 배포가 안되있어서
직접 만든 플러그인입니다.

자동 할당이 켜져 있을시 플레이어가 최초 접속시에 0, 0 청크 주변으로 나선형으로 돌아가며 할당이 됩니다.
할당이 되어있는 플레이어는 자신이 할당받은 청크 밖으로 나가지 못합니다.
~~보트를 통해선 나갈수 있습니다~~~



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
