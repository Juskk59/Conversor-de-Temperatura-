@echo off
echo ============================================
echo    Deploy Automatico - Tomcat
echo    Projeto: mavenproject
echo ============================================
echo.

REM Configuracoes - AJUSTE SE NECESSARIO
set PROJECT_DIR=C:\Users\muril\eclipse-workspace\mavenproject
set TOMCAT_DIR=C:\Program Files\Apache Software Foundation\Tomcat 10.1
set TOMCAT_WEBAPPS=%TOMCAT_DIR%\webapps

REM Verificar se MongoDB esta rodando
echo [0/5] Verificando MongoDB...
docker ps | findstr mongodb >nul
if errorlevel 1 (
    echo AVISO: MongoDB nao encontrado! Iniciando...
    docker start mongodb 2>nul
    if errorlevel 1 (
        echo Tentando criar container MongoDB...
        docker run -d --name mongodb -p 27017:27017 mongo:latest
    )
    timeout /t 3
)
echo MongoDB: OK

echo.
echo [1/5] Compilando projeto...
cd "%PROJECT_DIR%"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo.
    echo ========================================
    echo ERRO: Falha na compilacao!
    echo Verifique os erros acima.
    echo ========================================
    pause
    exit /b 1
)
echo Compilacao: OK

echo.
echo [2/5] Parando Tomcat...
cd "%TOMCAT_DIR%\bin"
call shutdown.bat 2>nul
echo Aguardando Tomcat parar...
timeout /t 8 /nobreak

echo.
echo [3/5] Removendo versao anterior...
if exist "%TOMCAT_WEBAPPS%\mavenproject.war" (
    del /F /Q "%TOMCAT_WEBAPPS%\mavenproject.war"
    echo Removido: mavenproject.war
)
if exist "%TOMCAT_WEBAPPS%\mavenproject" (
    rmdir /S /Q "%TOMCAT_WEBAPPS%\mavenproject"
    echo Removido: pasta mavenproject
)
echo Limpeza: OK

echo.
echo [4/5] Copiando novo WAR...
copy "%PROJECT_DIR%\target\mavenproject.war" "%TOMCAT_WEBAPPS%\"
if errorlevel 1 (
    echo.
    echo ========================================
    echo ERRO: Falha ao copiar WAR!
    echo Verifique as permissoes.
    echo ========================================
    pause
    exit /b 1
)
echo Deploy: OK

echo.
echo [5/5] Iniciando Tomcat...
cd "%TOMCAT_DIR%\bin"
start "" "startup.bat"
echo Tomcat iniciando...

echo.
echo ============================================
echo    Deploy Concluido com Sucesso!
echo ============================================
echo.
echo Aguardando Tomcat inicializar (15s)...
timeout /t 15 /nobreak

echo.
echo Testando API...
curl -s http://localhost:8080/mavenproject/api/temperatures
if errorlevel 1 (
    echo.
    echo AVISO: API ainda nao esta respondendo.
    echo Aguarde mais alguns segundos e tente:
    echo   curl http://localhost:8080/mavenproject/api/temperatures
) else (
    echo.
    echo API: OK - Respondendo!
)

echo.
echo ============================================
echo URLs Importantes:
echo   Manager: http://localhost:8080/manager/html
echo   API:     http://localhost:8080/mavenproject/api/temperatures
echo ============================================
echo.
echo Deseja abrir o Manager no navegador? (S/N)
set /p OPEN_BROWSER=
if /i "%OPEN_BROWSER%"=="S" (
    start http://localhost:8080/manager/html
)

echo.
pause
