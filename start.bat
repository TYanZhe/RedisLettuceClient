@echo off
::设置程序或文件的完整路径（必选）
set curdir=%~dp0
set Program=%curdir%start.bat
::设置快捷方式名称（必选）
set LnkName=RedisLark
::设置程序的工作路径，一般为程序主目录，此项若留空，脚本将自行分析路径
set WorkDir=
::设置快捷方式显示的说明（可选）
set Desc=RedisClient
set SoftFile=redisLark-1.1.1

if  exist %USERPROFILE%\Desktop\%LnkName%.lnk (
	del /f /q %USERPROFILE%\Desktop\%LnkName%.lnk
)
if not defined WorkDir call:GetWorkDir "%Program%"
(echo Set WshShell=CreateObject("WScript.Shell"^)
echo strDesKtop=WshShell.SPEcialFolders("DesKtop"^)
echo Set oShellLink=WshShell.CreateShortcut(strDesKtop^&"\%LnkName%.lnk"^)
echo oShellLink.TargetPath="%Program%"
echo oShellLink.WorkingDirectory="%WorkDir%"
echo oShellLink.Windowstyle=1
echo oShellLink.Description="%Desc%"
echo oShellLink.IconLocation = "%curdir%logo.ico"
echo oShellLink.Save)>makelnk.vbs
makelnk.vbs
del /f /q makelnk.vbs
set isExist=0
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %SoftFile%`) do (
	echo find process %%a %%b
	set isExist=1
	set _pid=%%a
	set image_name=%%b
)
if "%isExist%"=="1" (
	echo  %SoftFile% is Running
	pause
	(echo Set ws=CreateObject("WScript.Shell"^)
	echo ws.appactivate %_pid%
	echo ws.sendkeys "{ENTER}"
	)>openExist.vbs
	openExist.vbs
	del /f /q openExist.vbs

) else (
	cd /d %curdir%
	start javaw -jar ./redisLark-1.1.1.jar
	exit
)



:GetWorkDir
set WorkDir=%~dp1
set WorkDir=%WorkDir:~,-1%
goto :eof

