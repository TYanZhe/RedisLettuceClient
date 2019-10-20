title redisLark;
@echo off
set curdir=%~dp0
cd /d %curdir%
start javaw -jar ./redisLark-1.1.1.jar
exit