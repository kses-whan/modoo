@echo Start Building...
@set /p YN=Press any key...
del /q /s AppRelease\Build\ > nul
mkdir AppRelease\Build\app\src\main\java\com\icure\kses\modoo
xcopy app\src\main\AndroidManifest.xml AppRelease\Build\app\src\main /y
xcopy app\build.gradle AppRelease\Build\app\ /y
xcopy app\proguard-rules.pro AppRelease\Build\app\ /y
xcopy app\google-services.json AppRelease\Build\app\ /y
@REM START /MIN /WAIT .\tools\Utils\fnr.exe --silent --cl --dir "%CD%\AppRelease\Build\app" --fileMask "build.gradle" --caseSensitive --find ">modoo<" --replace ">modoo<"

@REM DuoShield JAVA 파일 복사
mkdir AppRelease\Build\allatori
xcopy allatori\*.* AppRelease\Build\allatori\ /y
xcopy app\allatori.xml AppRelease\Build\app\ /y
@REM START /MIN /WAIT .\tools\Utils\fnr.exe --silent --cl --dir "%CD%\AppRelease\Build\app" --fileMask "allatori.xml" --caseSensitive --find "com.icure.kses.modoo" --replace "com.icure.kses.modoo"

xcopy app\src\main\java\com\icure\kses\modoo\*.* AppRelease\Build\app\src\main\java\com\icure\kses\modoo\ /e /s /y
mkdir AppRelease\Build\app\src\main\aidl\com\android
xcopy app\src\main\aidl\com\android\*.* AppRelease\Build\app\src\main\aidl\com\android\ /e /s /y
mkdir AppRelease\Build\app\src\main\aidl\com\icure\kses\modoo
xcopy app\src\main\aidl\com\icure\kses\modoo\*.* AppRelease\Build\app\src\main\aidl\com\icure\kses\modoo\ /e /s /y
mkdir AppRelease\Build\app\src\main\java\com\twotis\nativelib
xcopy app\src\main\java\com\twotis\nativelib\*.* AppRelease\Build\app\src\main\java\com\twotis\nativelib\ /e /s /y
mkdir AppRelease\Build\app\src\main\res
xcopy app\src\main\res\*.* AppRelease\Build\app\src\main\res\ /e /s /y

@REM START /MIN /WAIT .\tools\Utils\fnr.exe --silent --cl --dir "%CD%\AppRelease\Build" --fileMask "*.*" --includeSubDirectories --skipBinaryFileDetection --caseSensitive --find "com.icure.kses.modoo" --replace "com.icure.kses.modoo"

.\tools\Utils\rmbom.exe "%CD%\AppRelease\Build"

mkdir AppRelease\Build\cert
xcopy cert\*.* AppRelease\Build\cert\ /e /s /y
mkdir AppRelease\Build\app\src\main\assets
xcopy app\src\main\assets\*.* AppRelease\Build\app\src\main\assets\ /e /s /y

mkdir AppRelease\Build\libs
xcopy app\libs\*.* AppRelease\Build\app\libs\ /e /s /y
xcopy app\src\main\jniLibs\*.* AppRelease\Build\app\src\main\jniLibs\ /e /s /y

mkdir AppRelease\Build\.settings
xcopy .settings\*.* AppRelease\Build\.settings\ /e /s /y
mkdir AppRelease\Build\app\src\main\jni
xcopy AppRelease\Modoo-PROD\*.* AppRelease\Build\  /e /s /y

@REM START /MIN /WAIT .\tools\Utils\fnr.exe --silent --cl --dir "%CD%\AppRelease\Build" --fileMask "AndroidManifest.xml" --includeSubDirectories --caseSensitive --skipBinaryFileDetection --find "app_name" --replace "app_name_dev"
START /MIN /WAIT .\tools\Utils\fnr.exe --silent --cl --dir "%CD%\AppRelease\Build" --fileMask "AndroidManifest.xml" --includeSubDirectories --caseSensitive --skipBinaryFileDetection --find "2bb1584ecac5d627e77d9284daa125ad" --replace "28ec75a07763213394bcae0cf34850da"

xcopy app\src\main\jni\*.* AppRelease\Build\app\src\main\jni\ /e /s /y
xcopy *.gradle AppRelease\Build\
xcopy gradlew AppRelease\Build\
xcopy gradlew.bat AppRelease\Build\
xcopy *.txt AppRelease\Build\
xcopy *.properties AppRelease\Build\

mkdir AppRelease\Build\gradle
xcopy gradle\*.* AppRelease\Build\gradle\ /e /s /y

@echo choice /t 5 /d n > nul

cd AppRelease\Build
call gradlew assembleRelease

cd ..\..
@REM @if /i "%YN%" == "y" goto INSTALL
@REM @if /i "%YN%" == "n" goto NO
@REM :INSTALL
@REM call gradle-install-modoo.bat
@REM :NO
pause
:END
