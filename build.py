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

print("빌드중...")

subprocess.run(["./gradlew", "jar"])

print("\nSSH 접속...")

client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

client.connect(_config.SSH_ADDRESS, username=_config.SSH_USERNAME, password=_config.SSH_PASSWORD)

print("빌드 파일 전송...")

sftp = client.open_sftp()
sftp.put(_config.COPY_PATH, _config.PASTE_PATH)

print("서버 명령 전송...")

channel = client.invoke_shell()

channel.send(f"screen -d -r {_config.SCREEN_NAME} \n")
time.sleep(1)
channel.send("reload confirm \n")

print("리로드 전송을 완료했습니다!")