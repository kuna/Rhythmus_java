Rhythmus Java Edition
=====================

BMS Player for Android

libGdx engine based BMS Player.

* 너무 용량이 큰 BMS 파일을 넣으면 메모리 부족으로 튕길 수 있으며, BMS 파일 갯수가 많아도 로딩하는 데 한세월 걸리니 유의하십시오.

* Android SoundPool의 제약 및 윈도우 libGdx의 사운드 제약 등으로 인해서 8-bit wav 파일과 같은 경우 제대로 소리가 재생되지 않을 수 있으니 유의하십시오.

TODO
---------------
- 3D-Note for mobile <code>작업 진행중이고 주석을 지우면 플레이도 가능하나 아직 불완전합니다.</code>
- Fully-supporting BMS file-format
- supporting Transparent key sound

Unsupported Due to machine/library limit
---------------
- Unstable Audio Playback
- Unsupporting movie play
- Colorkey (backgrounds, resources, etc. you should use PNG to get transparency)

Bugfix & Update
---------------

##### 130824

1. 곡 개수가 적을때 정상적으로 선곡이 안 되는 문제 수정
2. 노트 밀림 표시 추가
3. 마지막 비트에서 멈추지 않도록 수정
4. 플레이 화면 마지막에 여유분의 시간을 추가함
5. 안드로이드에서 입력이 없을 시 화면이 꺼지지 않도록 함

##### 130825

1. 이제 마디 길이를 제대로 처리합니다.
2. BMS Caching 기능을 추가하였습니다. (hash값이 바뀌어 이전 버전과의 스코어 연동 불능 / 12:31에 버그 수정)
3. getBeatFromTime 함수에 있던 버그 수정 (이제 L99999999999^999999999999가 제대로 돌아갑니다!)
4. beat/time 정밀도 향상
5. 안드로이드의 경우 키음들이 자동으로 음악 플레이어 라이브러리에 등록되는 걸 막았습니다 (.nomedia)
6. #RANDOM 분기 구문 지원, #STP 명령어 추가 지원
7. 롱노트 구현 중 (테스트 단계)

##### 130826

1. Fixed Sorting(Comparsion) error (occured at some BMS)

##### 130829

1. 드디어! 롱노트를 정상적으로 지원합니다 (130829 19:41 수정)
2. 선곡파일 로딩 시에 Select input을 받지 않도록 수정

##### 140712

방학을 맞이하야 + 여러 사람들의 성원으로 인한 무려 1년만의 업데이트...

1. 원래 edit 기능을 넣으려고 하였으나 이 부분을 [sabuneditor](https://github.com/kuna/SabunEditor_Android)로 이전하였습니다.
2. BMS Parser 엔진이 별개로 분리되어 [BMSJava](https://github.com/kuna/BMSJava) 라이브러리로 제공되고 있습니다. BMS 파싱 및 처리 관련 문제도 함께 수정되었습니다. 
3. 게임 엔진 자체를 모두 refactoring 하였습니다. <code>완벽하진 않고, Scene_Play_Setting과 같이 잘못 리펙토링한 코드도 있지만 이 정도면 전보단 훨씬 낫지!</code>
4. 외부에서 Intent를 주어서 BMS를 플레이 시킬 수 있습니다. 인자는 다음과 같습니다.
```java
intent.putExtra("Beat", 0);					// 기본값: 0
intent.putExtra("File", "test.bme");		// 필수로 입력해야 함
intent.putExtra("RemoveAfterPlay", false);	// 기본값: false
```
5. 키설정 기능 및 화면을 추가하였습니다.
6. 다양한 부분에서의 리소스 수정 및 추가.
7. 5K, 5K+SC, 7K, 7K+SC, 7K+SC for PC, 14K+SC for PC의 다양한 모드 지원.
8. 모바일 기기에서의 인간적인 플레이를 위한 판정 완화 <code>2배 완화되었습니다</code>
9. Zip archive 파일 지원 <code>zip 파일 안에 별개의 폴더 없이 곧바로 bms와 관련 리소스들이 들어있어야 합니다.</code>
10. 그 이외 플레이 도중 back 버튼이나 잘못된 setting 등의 문제로 인해 튕기는 잡다한 버그 수정.


How to Use
---------------

1. /sdcard/BMS 에 BMS 폴더 또는 압축 파일(.zip)를 넣으세요.
2. 프로그램을 켭니다. 안드로이드(모바일)의 경우 첫 실행 시간이 다소 오래 걸릴수 있으니 잠시만 기다려 주세요.
3. 마음껏 플레이 합니다!


Tip
---------------

1. 안드로이드에서 프로그램을 끄고 다시 켰는데 검은 화면만 나온다면, 잔류중인 리소스가 남아있을 가능성이 있습니다. 메모리 정리를 하고 나서 다시 켜 주세요.



Claim
---------------

* follows BSD license.