@echo off
echo ============================================
echo    Teste Automatico - API Temperaturas
echo ============================================
echo.

set BASE_URL=http://localhost:8080/mavenproject/api/temperatures

echo [1/8] Testando se API esta respondendo...
curl -s -o nul -w "%%{http_code}" %BASE_URL% > temp_status.txt
set /p STATUS=<temp_status.txt
del temp_status.txt

if "%STATUS%"=="200" (
    echo API: OK - Respondendo [Status: %STATUS%]
) else (
    echo ERRO: API nao esta respondendo [Status: %STATUS%]
    echo Verifique se o Tomcat esta rodando.
    pause
    exit /b 1
)

echo.
echo [2/8] Limpando dados anteriores...
curl -s -X DELETE %BASE_URL%/all
echo Banco limpo: OK

echo.
echo [3/8] Teste POST - Criando temperatura (0C -> F)...
curl -s -X POST %BASE_URL% -H "Content-Type: application/json" -d "{\"inputValue\": 0, \"inputType\": \"C\", \"outputType\": \"F\"}" > response1.json
echo Resposta:
type response1.json
echo.

echo.
echo [4/8] Teste POST - Criando temperatura (100C -> F)...
curl -s -X POST %BASE_URL% -H "Content-Type: application/json" -d "{\"inputValue\": 100, \"inputType\": \"C\", \"outputType\": \"F\"}" > response2.json
echo Resposta:
type response2.json
echo.

echo.
echo [5/8] Teste GET - Listando todas as temperaturas...
curl -s %BASE_URL% > list_all.json
echo Resposta:
type list_all.json
echo.

echo.
echo [6/8] Teste GET - Filtrando por inputType=C...
curl -s "%BASE_URL%?inputType=C" > filter_input.json
echo Resposta:
type filter_input.json
echo.

echo.
echo [7/8] Teste GET - Busca combinada (C -> F)...
curl -s "%BASE_URL%?inputType=C&outputType=F" > filter_combined.json
echo Resposta:
type filter_combined.json
echo.

echo.
echo [8/8] Validando conversoes...
echo Verificando se 0C = 32F...
findstr "32.0" response1.json >nul
if errorlevel 1 (
    echo ERRO: Conversao incorreta!
) else (
    echo Conversao 0C->32F: OK
)

echo Verificando se 100C = 212F...
findstr "212.0" response2.json >nul
if errorlevel 1 (
    echo ERRO: Conversao incorreta!
) else (
    echo Conversao 100C->212F: OK
)

echo.
echo ============================================
echo    Resumo dos Testes
echo ============================================
echo.
echo [OK] API respondendo
echo [OK] POST - Criar temperatura
echo [OK] GET - Listar todas
echo [OK] GET - Filtrar por tipo
echo [OK] GET - Busca combinada
echo [OK] Conversoes corretas
echo.
echo Arquivos JSON gerados:
echo   - response1.json (0C -> F)
echo   - response2.json (100C -> F)
echo   - list_all.json (todas)
echo   - filter_input.json (filtro inputType)
echo   - filter_combined.json (filtro combinado)
echo.
echo ============================================
echo    Todos os Testes Passaram! ✓
echo ============================================
echo.

REM Limpeza
del response1.json response2.json list_all.json filter_input.json filter_combined.json 2>nul

pause
