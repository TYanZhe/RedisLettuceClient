@echo off
set char=redisLark-1.1.1
echo char : %char%
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %char%`) do (
	echo find process %%a %%b
	set pid=%%a
	set image_name=%%b
	echo now will kill process : pid %pid%, image_name %image_name%
	TASKKILL /F /FI "PID eq %%a"
	echo %pid% is kill
)
pause
