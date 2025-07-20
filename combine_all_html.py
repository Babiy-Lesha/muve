import os

# Папки микросервисов
services = [
    "userService",
    "movieservice",
    "vaadinui",
    "eurekaServer",
    "apiGateway"
]

base_path = r"C:\programirovanie\proectMove"
output_path = os.path.join(base_path, "full_system_dump.html")

html_parts = []

# Заголовок HTML
html_parts.append("""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Full Microservice Dump</title>
</head>
<body>
<h1>Сводный отчет по микросервисам</h1>
""")

# Чтение project_dump.html каждого сервиса
for service in services:
    html_file = os.path.join(base_path, service, "project_dump.html")
    if os.path.isfile(html_file):
        with open(html_file, "r", encoding="utf-8") as f:
            content = f.read()

            # Убираем <html>, <body> и дублирующий <head> если есть
            stripped = content.replace("<html>", "").replace("</html>", "") \
                              .replace("<body>", "").replace("</body>", "") \
                              .replace("<head>", "").replace("</head>", "")

            html_parts.append(f"<hr><h2>{service}</h2>\n{stripped}")
    else:
        html_parts.append(f"<hr><h2>{service}</h2>\n<p><i>Файл project_dump.html не найден</i></p>")

# Закрытие HTML
html_parts.append("</body></html>")

# Сохраняем итог
with open(output_path, "w", encoding="utf-8") as out_file:
    out_file.write("\n".join(html_parts))

print(f"✅ Сводный HTML готов: {output_path}")
