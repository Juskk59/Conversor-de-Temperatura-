@echo off
echo ============================================
echo    Script de Verificacao - Ambiente Deploy
echo ============================================
echo.

echo [1] Verificando Java...
java -version 2>&1 | findstr "version"
if errorlevel 1 (
    echo ERRO: Java nao encontrado!
    echo Instale o JDK 17 ou superior.
) else (
    echo Java: OK
)

echo.
echo [2] Verificando Maven...
mvn -version 2>&1 | findstr "Apache Maven"
if errorlevel 1 (
    echo ERRO: Maven nao encontrado!
    echo Instale o Maven e adicione ao PATH.
) else (
    echo Maven: OK
)

echo.
echo [3] Verificando Docker...
docker --version 2>nul
if errorlevel 1 (
    echo AVISO: Docker nao encontrado!
    echo Instale o Docker Desktop para Windows.
    echo Ou instale o MongoDB localmente.
) else (
    echo Docker: OK
)

echo.
echo [4] Verificando MongoDB...
docker ps 2>nul | findstr mongodb
if errorlevel 1 (
    echo AVISO: MongoDB nao esta rodando!
    echo Execute: docker run -d --name mongodb -p 27017:27017 mongo:latest
) else (
    echo MongoDB: OK - Container rodando
)

echo.
echo [5] Verificando Tomcat...
set TOMCAT_DIR=C:\Program Files\Apache Software Foundation\Tomcat 10.1
if exist "%TOMCAT_DIR%" (
    echo Tomcat: OK - Encontrado em %TOMCAT_DIR%
) else (
    echo ERRO: Tomcat nao encontrado!
    echo Instale o Apache Tomcat 10.1
    echo Baixe em: https://tomcat.apache.org/download-10.cgi
)

echo.
echo [6] Verificando porta 8080...
netstat -an | findstr "8080.*LISTENING" >nul
if errorlevel 1 (
    echo Porta 8080: LIVRE
) else (
    echo Porta 8080: EM USO
    echo Processo usando porta 8080:
    netstat -ano | findstr ":8080.*LISTENING"
)

echo.
echo [7] Verificando projeto...
set PROJECT_DIR=C:\Users\muril\eclipse-workspace\mavenproject
if exist "%PROJECT_DIR%\pom.xml" (
    echo Projeto: OK - Encontrado
    if exist "%PROJECT_DIR%\target\mavenproject.war" (
        echo WAR: OK - Ja compilado
    ) else (
        echo WAR: Nao encontrado - Execute: mvn clean package
    )
) else (
    echo ERRO: Projeto nao encontrado em %PROJECT_DIR%
)

echo.
echo ============================================
echo    Resumo da Verificacao
echo ============================================
echo.
echo Itens OK: Execute deploy.bat
echo Itens com ERRO: Corrija antes de prosseguir
echo Itens com AVISO: Opcionais, mas recomendados
echo.
pause
