import os
from pygments import highlight
from pygments.lexers import JavaLexer, XmlLexer, YamlLexer, TextLexer
from pygments.formatters import HtmlFormatter

project_root = r"C:\programirovanie\proectMove\vaadinui"
output_path = os.path.join(project_root, "project_dump.html")

formatter = HtmlFormatter(full=True, linenos=True, style="monokai")
html_parts = []

def process_file(file_path, lexer):
    with open(file_path, "r", encoding="utf-8") as f:
        code = f.read()
    highlighted_code = highlight(code, lexer, formatter)
    return f"<h2>{file_path}</h2>\n{highlighted_code}"

# Java файлы
for root, _, files in os.walk(os.path.join(project_root, "src", "main", "java")):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            html_parts.append(process_file(file_path, JavaLexer()))

# Конфигурационные и Docker-файлы
other_files = {
    "pom.xml": XmlLexer(),
    "Dockerfile": TextLexer(),
    "docker-compose.yml": YamlLexer(),
    "application.yml": YamlLexer()
}

for file, lexer in other_files.items():
    file_path = os.path.join(project_root, "src", "main", "resources", file) \
        if file == "application.yml" else os.path.join(project_root, file)
    if os.path.isfile(file_path):
        html_parts.append(process_file(file_path, lexer))

# Запись HTML-результата
with open(output_path, "w", encoding="utf-8") as f:
    f.write("\n".join(html_parts))

print(f"✅ Готово! Открой в браузере: {output_path}")
