@echo off
ECHO "--Prepare config file--"
If Not Exist "%CD:~0,3%config\logback.xml" (
	If Not Exist "%CD:~0,3%config" mkdir "%CD:~0,3%config"
	copy "src\test\resources\config\logback.xml" "%CD:~0,3%config\logback.xml"
)
ECHO "----JACOCO Report----"
call mvn clean verify surefire-report:report -Dmaven.test.failure.ignore=true > junit_logs.txt 2>&1

rem @echo off
rem ECHO "----Surefire report----"
rem call mvn surefire-report:report

rem @echo off
rem ECHO "----Generate CSS/JS----"
call mvn site -DgenerateReports=false >> junit_logs.txt 2>&1

ECHO "----Generate report done.----"
PAUSE