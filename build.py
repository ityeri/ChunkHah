"""
required:

pip install paramiko

and

_config.py:
SSH_ADDRESS = "example.com"
SSH_USERNAME = "username"
SSH_PASSWORD = "password"

COPY_PATH = "build/libs/plugin.jar"
PASTE_PATH = "server/plugins/plugin.jar"
SCREEN_NAME = "screen-name"
"""

import subprocess
import _config
import paramiko
import time
import os

def dot_clean(directory):
    # directory 안의 파일과 폴더를 재귀적으로 탐색
    for root, dirs, files in os.walk(directory):
        for file in files:
            # 파일이 '._'로 시작하는지 확인
            if file.startswith("._"):
                file_path = os.path.join(root, file)
                try:
                    os.remove(file_path)
                    print(f"파일 삭제됨: {file_path}")
                except Exception as e:
                    print(f"파일 삭제 실패: {file_path} - {e}")

while True:

    print("메타데이터 파일 제거...")
    dot_clean(os.getcwd())
    print("메타데이터 파일 제거 완료")

    print("빌드중...")
    subprocess.run(["./gradlew", "jar"])

    print("\nSSH 접속...")
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    client.connect(_config.SSH_ADDRESS, username=_config.SSH_USERNAME, password=_config.SSH_PASSWORD)

    print("빌드 파일 전송...")
    sftp = client.open_sftp()
    sftp.put(_config.COPY_PATH, _config.PASTE_PATH)
    print("빌드 파일 전송 완료")

    print("메타데이터 파일 제거...")
    dot_clean(os.getcwd())
    print("메타데이터 파일 제거 완료")


    break

# print("서버 명령 전송...")
#
# channel = client.invoke_shell()
#
# channel.send(f"screen -d -r {_config.SCREEN_NAME} \n")
# time.sleep(1)
# channel.send("reload confirm \n")
#
# print("리로드 전송을 완료했습니다!")