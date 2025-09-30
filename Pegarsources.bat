@echo off
REM Baixar código-fonte do site e salvar em sources.txt na pasta Downloads silenciosamente
set downloadPath=%USERPROFILE%\Downloads\sources.txt
curl https://eventos.ifgoiano.edu.br/integra2025/ -o "%downloadPath%" >nul 2>&1
